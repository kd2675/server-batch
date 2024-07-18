package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.music.api.vo.BugsApiListVO;
import com.example.batch.service.music.api.vo.BugsApiVO;
import com.example.batch.service.music.database.rep.jpa.music.MusicEntity;
import com.example.batch.service.music.database.rep.jpa.music.MusicREP;
import com.example.batch.service.music.database.rep.jpa.music.PlaylistEntity;
import com.example.batch.service.music.database.rep.jpa.music.PlaylistREP;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsREP;
import com.example.batch.service.news.database.rep.jpa.news.NewsSpec;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsEntity;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsREP;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsSpec;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;
import com.example.batch.utils.BugsApiUtil;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.YoutubeApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookSVCImpl implements WebhookSVC, WebhookCMD {
    private final MattermostUtil mattermostUtil;
    private final BugsApiUtil bugsApiUtil;
    private final YoutubeApiUtil youtubeApiUtil;

    private final NewsREP newsREP;
    private final OldNewsREP oldNewsREP;
    private final MusicREP musicREP;
    private final PlaylistREP playlistREP;

    @Transactional
    @Override
    public void cmdCall(WebhookVO webhookVO) {
        String cmd = webhookVO.getText().split(" ")[0];

        Map<String, Runnable> commandMap = new HashMap<>();
        commandMap.put(WebhookEnum.COMMAND_0.getKey(), this::time);
        commandMap.put(WebhookEnum.COMMAND_1.getKey(), this::uptime);
        commandMap.put(WebhookEnum.COMMAND_2.getKey(), () -> this.news(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_3.getKey(), () -> this.oldNews(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_4.getKey(), this::music);
        commandMap.put(WebhookEnum.COMMAND_5.getKey(), () -> this.musicSearch(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_6.getKey(), () -> this.playlist(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_7.getKey(), () -> this.playlistAdd(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_8.getKey(), () -> this.playlistRemove(webhookVO));

        for (WebhookEnum webhookEnum : WebhookEnum.values()) {
            commandMap.put(webhookEnum.getShortKey(), commandMap.get(webhookEnum.getKey()));
        }

        commandMap.getOrDefault(cmd, this::help).run();
    }

    private void test() {
    }

    //pageNo 있으면 차례로
    //pageNo 없으면 랜덤
    @Transactional(readOnly = true)
    @Override
    public void playlist(WebhookVO webhookVO) {
        Integer pageNo = 0;
        Integer pagePerCnt = 100;
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");

        Page<PlaylistEntity> all = playlistREP.findAll(pageable);
        List<PlaylistEntity> content = all.getContent();

        if (!content.isEmpty()) {
            mattermostUtil.sendBobChannel(this.convertMattermostStr(content));
        }
    }

    private String convertMattermostStr(List<PlaylistEntity> entities) {
        StringBuilder sb = new StringBuilder();
//        String header = "| id | title | singer | pubDate | youtube |\n";
//        String line = "| :-:|:-:|:--:|:-:|:-: |\n";
//        sb.append(header)
//                .append(line);

        entities.forEach(v -> {
            sb.append("|");
            sb.append(v.getId());
            sb.append("|");
            sb.append(v.getTitle());
            sb.append("|");
            sb.append(v.getSinger());
            sb.append("|");
            sb.append(v.getPubDate());
            sb.append("|");
            sb.append(v.getYoutubeLink() != null ? "[Link]" + "(" + v.getYoutubeLink() + ")" : "-");
            sb.append("|");
            sb.append("\n");
        });

        return sb.toString();
    }

    @Transactional
    @Override
    public void playlistAdd(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            Optional<MusicEntity> musicEntity = musicREP.findById(id);
            musicEntity.ifPresent((v) -> {
                Optional<PlaylistEntity> byNo = playlistREP.findByNo(v.getNo());

                byNo.ifPresentOrElse(
                        (b) -> {

                        },
                        () -> {
                            PlaylistEntity playlistEntity = v.convertToPlaylistEntity();
                            playlistREP.save(playlistEntity);
                        }
                );
            });
            mattermostUtil.sendBobChannel("완료");
        } catch (Exception e) {
            mattermostUtil.sendBobChannel("에러");
            log.error("playlistAdd error", e);
        }
    }

    @Transactional
    @Override
    public void playlistRemove(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            playlistREP.deleteById(id);

            mattermostUtil.sendBobChannel("완료");
        } catch (Exception e) {
            mattermostUtil.sendBobChannel("에러");
            log.error("playlistAdd error", e);
        }
    }

    @Transactional
    @Override
    public void musicSearch(WebhookVO webhookVO) {
        try {
            String[] split = parseSplitText(webhookVO.getText());
            if (split.length < 2 || split.length > 4) {
                this.help();
                return;
            }

            String searchText = split[1];
            int[] pagingInfo = getPagingInfo(split);

            ResponseEntity<String> conn = bugsApiUtil.conn("track", searchText, pagingInfo[0], pagingInfo[1]);
            BugsApiVO bugsApiVO = parseResponse(conn.getBody());

            List<MusicEntity> musicList = bugsApiVO.getList().stream()
                    .map(this::processTrack)
                    .collect(Collectors.toList());

            if (!musicList.isEmpty()) {
                mattermostUtil.sendBobChannel(this.mattermostConvertMsg(musicList));
            }
        } catch (Exception e) {
            this.help();
            log.error("Error in musicSearch", e);
        }
    }

    private String[] parseSplitText(String text) {
        if (text.contains("'") && text.length() == text.replace("'", "").length() + 2) {
            return text.split("'");
        } else if (text.contains("\"") && text.length() == text.replace("\"", "").length() + 2) {
            return text.split("\"");
        } else {
            return text.split(" ");
        }
    }

    private int[] getPagingInfo(String[] split) {
        int pageNo = 1, pagePerCnt = 5;
        if (split.length >= 3) {
            String[] paging = split[2].trim().split(" ");
            if (paging.length == 2) {
                pageNo = Integer.parseInt(paging[0]) + 1;
                pagePerCnt = Math.min(Integer.parseInt(paging[1]), 10);
            }
        }
        return new int[]{pageNo, pagePerCnt};
    }

    private BugsApiVO parseResponse(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(body, BugsApiVO.class);
    }

    private MusicEntity processTrack(BugsApiListVO track) {
        return musicREP.findBySlctAndNo("b", track.getTrackId())
                .map(entity -> updateExistingTrack(entity, track))
                .orElseGet(() -> createNewTrack(track));
    }

    private MusicEntity updateExistingTrack(MusicEntity entity, BugsApiListVO track) {
        if (StringUtils.isEmpty(entity.getYoutubeLink())) {
            entity.updYoutubeLink(getYoutubeLink(track.getTrackTitle(), track.getArtists().get(0).getArtistNm()));
        }
        if (StringUtils.isEmpty(entity.getAlbumImg())) {
            entity.updAlbumImg(getAlbumImageUrl(String.valueOf(track.getAlbum().getImage().get("path"))));
        }
        return musicREP.save(entity);
    }

    private MusicEntity createNewTrack(BugsApiListVO track) {
        return musicREP.save(MusicEntity.builder()
                .slct("b")
                .no(track.getTrackId())
                .title(track.getTrackTitle())
                .singer(track.getArtists().get(0).getArtistNm())
                .album(track.getAlbum().getTitle())
                .albumImg(getAlbumImageUrl(String.valueOf(track.getAlbum().getImage().get("path"))))
                .pubDate(track.getUpdDt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .youtubeLink(getYoutubeLink(track.getTrackTitle(), track.getArtists().get(0).getArtistNm()))
                .build());
    }

    private String getYoutubeLink(String title, String artist) {
        ResponseEntity<String> youtubeConn = youtubeApiUtil.conn(title + " " + artist);
        String youtubeBody = youtubeConn.getBody();
        String subStr = youtubeBody.substring(youtubeBody.indexOf("\"videoId\":\"") + "\"videoId\":\"".length());
        String id = subStr.substring(0, subStr.indexOf("\""));
        return "https://www.youtube.com/watch?v=" + id;
    }

    private String getAlbumImageUrl(String imagePath) {
        return "![](https://image.bugsm.co.kr/album/images/200" + imagePath + "?version=20240419015238.0 =50x50)";
    }

    private String mattermostConvertMsg(List<MusicEntity> musicEntity) {
        StringBuilder sb = new StringBuilder();
        String header = "| id | albumImg | title | singer | pubDate | youtube |\n";
        String line = "| :-:|:-:|:-:|:--:|:-:|:--: |\n";
        sb.append(header)
                .append(line);

        musicEntity.forEach(v -> {
            String albumImg = v.getAlbumImg();
            Long no = v.getId();
            String title = v.getTitle();
            String singer = v.getSinger();
            LocalDate pubDate = v.getPubDate();
            String youtubeLink = v.getYoutubeLink() != null ? "[Link]" + "(" + v.getYoutubeLink() + ")" : "-";

            sb.append("|");
            sb.append(no);
            sb.append("|");
            sb.append(albumImg);
            sb.append("|");
            sb.append(title);
            sb.append("|");
            sb.append(singer);
            sb.append("|");
            sb.append(pubDate);
            sb.append("|");
            sb.append(youtubeLink);
            sb.append("|");
            sb.append("\n");
        });

        return sb.toString();
    }

    @Transactional(readOnly = true)
    @Override
    public void music() {
        musicREP.findMusicRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();
                    String singer = musicEntity.getSinger();
                    String pubDate = musicEntity.getPubDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String youtubeLink = musicEntity.getYoutubeLink() != null ? "[Link]" + "(" + musicEntity.getYoutubeLink() + ")" : "-";

                    String str = title + " " + singer + " " + pubDate + " " + youtubeLink;
                    mattermostUtil.sendBobChannel(str);
                },
                () -> {

                }
        );
    }

    private boolean isValidInput(String[] args) {
        return args.length == 4 && Integer.parseInt(args[3]) <= 10;
    }

    @Transactional(readOnly = true)
    @Override
    public void news(WebhookVO webhookVO) {
        String[] args = webhookVO.getText().split(" ");
        if (!isValidInput(args)) {
            this.help();
            return;
        }

        String searchText = args[1];
        int pageNo = Integer.parseInt(args[2]);
        int pagePerCnt = Integer.parseInt(args[3]);

        List<NewsEntity> newsEntities = searchNews(searchText, pageNo, pagePerCnt);
        if (!newsEntities.isEmpty()) {
            mattermostUtil.sendBobChannel(convertNewsMattermostMessage(newsEntities));
        }
    }

    private List<NewsEntity> searchNews(String searchText, int pageNo, int pagePerCnt) {
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        List<String> searchTerms = Arrays.asList(searchText.split(","));
        return newsREP.findAll(NewsSpec.searchWith(searchTerms), pageable).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public void oldNews(WebhookVO webhookVO) {
        String[] args = webhookVO.getText().split(" ");
        if (!isValidInput(args)) {
            this.help();
            return;
        }

        String searchText = args[1];
        int pageNo = Integer.parseInt(args[2]);
        int pagePerCnt = Integer.parseInt(args[3]);

        List<OldNewsEntity> oldNewsEntities = searchOldNews(searchText, pageNo, pagePerCnt);
        if (!oldNewsEntities.isEmpty()) {
            mattermostUtil.sendBobChannel(convertOldNewsMattermostMessage(oldNewsEntities));
        }
    }

    private List<OldNewsEntity> searchOldNews(String searchText, int pageNo, int pagePerCnt) {
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        List<String> searchTerms = Arrays.asList(searchText.split(","));
        return oldNewsREP.findAll(OldNewsSpec.searchWith(searchTerms), pageable).getContent();
    }

    private String convertNewsMattermostMessage(List<NewsEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| 시각 | 제목 |\n";
        String line = "| :-:|:--: |\n";
//        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
//        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<NewsEntity> q = new LinkedList<>(entityList);
        while (!q.isEmpty()) {
            String content = "";
            for (int i = 0; i < 1; i++) {
                if (q.isEmpty()) {
                    break;
                }
                NewsEntity remove = q.remove();

                content += "| " + dtf.format(remove.getPubDate())
                        + " | " + "[" + remove.getTitle().replaceAll(regexEmojis, "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("♥", "")
                        .replace("|", "") + "]" + "(" + remove.getLink() + ")";
            }
            content += " |\n";
            result.append(content);
        }

        return result.toString();
    }

    private String convertOldNewsMattermostMessage(List<OldNewsEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| 시각 | 제목 |\n";
        String line = "| :-:|:--: |\n";
//        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
//        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<OldNewsEntity> q = new LinkedList<>(entityList);
        while (!q.isEmpty()) {
            String content = "";
            for (int i = 0; i < 1; i++) {
                if (q.isEmpty()) {
                    break;
                }
                OldNewsEntity remove = q.remove();

                content += "| " + dtf.format(remove.getPubDate())
                        + " | " + "[" + remove.getTitle().replaceAll(regexEmojis, "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("♥", "")
                        .replace("|", "") + "]" + "(" + remove.getLink() + ")";
            }
            content += " |\n";
            result.append(content);
        }

        return result.toString();
    }

    @Override
    public void help() {
        StringBuilder str = new StringBuilder();

        for (WebhookEnum webhook : WebhookEnum.values()) {
            str.append(webhook.getId())
                    .append(". ")
                    .append(webhook.getKey())
                    .append(", ")
                    .append(webhook.getShortKey())
                    .append(" : ")
                    .append(webhook.getValue())
                    .append("\n");
        }
        mattermostUtil.sendBobChannel(str.toString());
    }

    @Override
    public void time() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target =
                LocalDateTime.of(
                        now.getYear(),
                        now.getMonth(),
                        now.getDayOfMonth(),
                        18,
                        0,
                        0
                );

        if (now.isBefore(target)) {
            Duration between = Duration.between(now, target);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            LocalDateTime localDateTime =
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            Long.valueOf(hour).intValue(),
                            Long.valueOf(minute).intValue(),
                            Long.valueOf(seconds).intValue()
                    );

            String fullTime =
                    localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            String partTime =
                    localDateTime.minusHours(1)
                            .minusMinutes(30)
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            StringBuilder str = new StringBuilder();
            str.append(fullTime)
                    .append(" 남았습니다.");

            mattermostUtil.sendBobChannel(str.toString());
        } else {
            mattermostUtil.sendBobChannel("퇴근하세요");
        }
    }

    @Override
    public void uptime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target =
                LocalDateTime.of(
                        now.getYear(),
                        now.getMonth(),
                        now.getDayOfMonth(),
                        8,
                        30,
                        0
                );

        if (now.isAfter(target)) {
            Duration between = Duration.between(target, now);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            String format =
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            Long.valueOf(hour).intValue(),
                            Long.valueOf(minute).intValue(),
                            Long.valueOf(seconds).intValue()
                    ).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            mattermostUtil.sendBobChannel(format + " 지났습니다.");
        } else {
            mattermostUtil.sendBobChannel("출근하세요");
        }
    }
}
