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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookSVCImpl implements WebhookSVC, WebhookCMD {
    private final MattermostUtil mattermostUtil;
    private final BugsApiUtil bugsApiUtil;

    private final NewsREP newsREP;
    private final OldNewsREP oldNewsREP;
    private final MusicREP musicREP;
    private final PlaylistREP playlistREP;

    @Transactional
    @Override
    public void cmdCall(WebhookVO webhookVO) {
        String text = webhookVO.getText();
        String[] split = text.split(" ");
        String cmd = split[0];

        if (cmd.equals(WebhookEnum.COMMAND_0.getKey()) || cmd.equals(WebhookEnum.COMMAND_0.getShortKey())) {
            this.time();
        } else if (cmd.equals(WebhookEnum.COMMAND_1.getKey()) || cmd.equals(WebhookEnum.COMMAND_1.getShortKey())) {
            this.uptime();
        } else if (cmd.equals(WebhookEnum.COMMAND_2.getKey()) || cmd.equals(WebhookEnum.COMMAND_2.getShortKey())) {
            this.news(webhookVO);
        } else if (cmd.equals(WebhookEnum.COMMAND_3.getKey()) || cmd.equals(WebhookEnum.COMMAND_3.getShortKey())) {
            this.oldNews(webhookVO);
        } else if (cmd.equals(WebhookEnum.COMMAND_4.getKey()) || cmd.equals(WebhookEnum.COMMAND_4.getShortKey())) {
            this.music();
        } else if (cmd.equals(WebhookEnum.COMMAND_5.getKey()) || cmd.equals(WebhookEnum.COMMAND_5.getShortKey())) {
            this.searchMusic(webhookVO);
        } else if (cmd.equals(WebhookEnum.COMMAND_6.getKey()) || cmd.equals(WebhookEnum.COMMAND_6.getShortKey())) {
            this.playlist(webhookVO);
        } else if (cmd.equals(WebhookEnum.COMMAND_7.getKey()) || cmd.equals(WebhookEnum.COMMAND_7.getShortKey())) {
            this.playlistAdd(webhookVO);
        } else if (cmd.equals(WebhookEnum.COMMAND_8.getKey()) || cmd.equals(WebhookEnum.COMMAND_8.getShortKey())) {
            this.playlistRemove(webhookVO);
        } else {
            this.help();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void playlist(WebhookVO webhookVO) {
        Integer pageNo = 0;
        Integer pagePerCnt = 100;
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "title");

        Page<PlaylistEntity> all = playlistREP.findAll(pageable);
        List<PlaylistEntity> content = all.getContent();

        if (!content.isEmpty()){
            mattermostUtil.sendBobChannel(this.convertMattermostStr(content));
        }
    }

    private String convertMattermostStr(List<PlaylistEntity> entities){
        StringBuilder sb = new StringBuilder();
        String header = "| id | title | singer | pubDate |\n";
        String line = "| :-:|:-:|:--:|:-: |\n";
        sb.append(header)
                .append(line);

        entities.forEach(v->{
            sb.append("|");
            sb.append(v.getId());
            sb.append("|");
            sb.append(v.getTitle());
            sb.append("|");
            sb.append(v.getSinger());
            sb.append("|");
            sb.append(v.getPubDate());
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
                PlaylistEntity playlistEntity = v.convertToPlaylistEntity();
                playlistREP.save(playlistEntity);
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
    public void searchMusic(WebhookVO webhookVO) {
        try {
            String[] split;

            if (webhookVO.getText().contains("\'")
                    && webhookVO.getText().length() == (webhookVO.getText().replace("\'", "").length() + 2)
            ) {
                split = webhookVO.getText().split("\'");
            } else if (webhookVO.getText().contains("\"")
                    && webhookVO.getText().length() == (webhookVO.getText().replace("\"", "").length() + 2)
            ) {
                split = webhookVO.getText().split("\"");
            } else {
                this.help();
                return;
            }
//            String[] split = webhookVO.getText().split(" ");
            if (split.length != 2 && split.length != 3) {
                this.help();
                return;
            }

            String searchText = split[1];


            int pageNo = 1;
            int pagePerCnt = 5;

            if (split.length == 3) {
                String[] paging = split[2].trim().split(" ");

                if (paging.length != 2 && paging.length > 0) {
                    this.help();
                    return;
                } else if (paging.length == 0) {

                } else {
                    pageNo = Integer.parseInt(paging[0]) + 1;
                    pagePerCnt = Integer.parseInt(paging[1]);
                    if (pagePerCnt > 10) {
                        this.help();
                        return;
                    }
                }
            }

            ResponseEntity conn = bugsApiUtil.conn("track", searchText, pageNo, pagePerCnt);

            String body = (String) conn.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            BugsApiVO bugsApiVO = objectMapper.readValue(body, BugsApiVO.class);
            List<BugsApiListVO> bugsApiVOS = bugsApiVO.getList();

            List<MusicEntity> list = new ArrayList<>();

            bugsApiVOS.forEach(v -> {
                musicREP.findBySlctAndNo("b", v.getTrackId()).ifPresentOrElse(
                        list::add,
                        () -> {
                            Date updDt = v.getUpdDt();

                            MusicEntity musicEntity = MusicEntity.builder()
                                    .slct("b")
                                    .no(v.getTrackId())
                                    .title(v.getTrackTitle())
                                    .singer(v.getArtists().get(0).getArtistNm())
                                    .album(v.getAlbum().getTitle())
                                    .pubDate(
                                            updDt.toInstant()
                                                    .atZone(ZoneId.systemDefault())
                                                    .toLocalDate()
                                    ).build();

                            MusicEntity save = musicREP.save(musicEntity);
                            list.add(save);
                        }
                );

            });

            StringBuilder str = new StringBuilder();

            list.forEach(v ->
                    str.append(this.mattermostConvertMsg(v)).append("\n")
            );

            if (!str.isEmpty()) {
                mattermostUtil.sendBobChannel(str.toString());
            }

        } catch (JsonProcessingException e) {
            this.help();
            log.error("{}", e);
        }
    }

    private String mattermostConvertMsg(MusicEntity musicEntity) {
        Long no = musicEntity.getId();
        String title = musicEntity.getTitle();
        String singer = musicEntity.getSinger();
        LocalDate pubDate = musicEntity.getPubDate();

        String str = no + " " + title + " " + singer + " " + pubDate;

        return str;
    }

    @Transactional(readOnly = true)
    @Override
    public void music() {
        musicREP.findMusicRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();
                    String singer = musicEntity.getSinger();
                    String pubDate = musicEntity.getPubDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String str = title + " " + singer + " " + pubDate;
                    mattermostUtil.sendBobChannel(str);
                },
                () -> {

                }
        );
    }

    @Transactional(readOnly = true)
    @Override
    public void news(WebhookVO webhookVO) {
        String[] split = webhookVO.getText().split(" ");
        if (split.length != 4) {
            this.help();
            return;
        }

        String searchText = split[1].replace(",", "&&");
        int pageNo = Integer.parseInt(split[2]);
        int pagePerCnt = Integer.parseInt(split[3]);
        if (pagePerCnt > 10) {
            this.help();
            return;
        }


        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        Page<NewsEntity> search2 = newsREP.findAll(NewsSpec.searchWith(Arrays.asList(split[1].split(","))), pageable);

        List<NewsEntity> content = search2.getContent();

        if (!content.isEmpty()) {
            mattermostUtil.sendBobChannel(convertNewsMattermostMessage(content));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void oldNews(WebhookVO webhookVO) {
        String[] split = webhookVO.getText().split(" ");
        if (split.length != 4) {
            this.help();
            return;
        }

        String searchText = split[1].replace(",", "&&");
        int pageNo = Integer.parseInt(split[2]);
        int pagePerCnt = Integer.parseInt(split[3]);
        if (pagePerCnt > 10) {
            this.help();
            return;
        }


        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        Page<OldNewsEntity> search2 = oldNewsREP.findAll(OldNewsSpec.searchWith(Arrays.asList(split[1].split(","))), pageable);

        List<OldNewsEntity> content = search2.getContent();

        if (!content.isEmpty()) {
            mattermostUtil.sendBobChannel(convertOldNewsMattermostMessage(content));
        }
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
