package com.example.batch.service.webhook.api.biz;

import com.example.batch.utils.vo.BugsApiListVO;
import com.example.batch.utils.vo.BugsApiVO;
import com.example.batch.service.webhook.database.rep.jpa.music.MusicEntity;
import com.example.batch.service.webhook.database.rep.jpa.music.MusicREP;
import com.example.batch.service.webhook.database.rep.jpa.music.PlaylistEntity;
import com.example.batch.service.webhook.database.rep.jpa.music.PlaylistREP;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.BugsApiUtil;
import com.example.batch.utils.ChromeDriverConnUtil;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.YoutubeApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MusicSVCImpl implements MusicSVC {
    private final String URL = "https://vibe.naver.com/track/";

    private final ChromeDriverConnUtil chromeDriverConnUtil;
    private final MattermostUtil mattermostUtil;
    private final BugsApiUtil bugsApiUtil;
    private final YoutubeApiUtil youtubeApiUtil;

    private final MusicREP musicREP;
    private final PlaylistREP playlistREP;

    @Override
    public void notRun(WebhookVO webhookVO){
        mattermostUtil.sendWebhookChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요", webhookVO);
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
            mattermostUtil.sendWebhookChannel(this.convertMattermostStr(content), webhookVO);
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
            mattermostUtil.sendWebhookChannel("완료", webhookVO);
        } catch (Exception e) {
            mattermostUtil.sendWebhookChannel("에러", webhookVO);
            log.error("playlistAdd error", e);
        }
    }

    @Transactional
    @Override
    public void playlistRemove(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            playlistREP.deleteById(id);

            mattermostUtil.sendWebhookChannel("완료", webhookVO);
        } catch (Exception e) {
            mattermostUtil.sendWebhookChannel("에러", webhookVO);
            log.error("playlistAdd error", e);
        }
    }

    @Transactional
    @Override
    public void musicSearch(WebhookVO webhookVO) {
        try {
            String[] split = parseSplitText(webhookVO.getText());
            if (split.length < 2 || split.length > 4) {
                this.notRun(webhookVO);
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
                mattermostUtil.sendWebhookChannel(this.mattermostConvertMsg(musicList), webhookVO);
            }
        } catch (Exception e) {
            this.notRun(webhookVO);
            log.error("Error in musicSearch", e);
        }
    }

    private String[] parseSplitText(String text) {
        if (text.contains("'") && text.length() == text.replace("'", "").length() + 2) {
            return text.split("'");
        } else if (text.contains("\"") && text.length() == text.replace("\"", "").length() + 2) {
            return text.split("\"");
        } else {
            if(text.split(" ")[1].startsWith("\'") && text.split(" ")[1].endsWith("\"")){
                throw new RuntimeException("parseSplitText error");
            }
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

    @Override
    public void musicPlay(WebhookVO webhookVO) {
        Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

        musicREP.findTop1ByIdOrderByIdDesc(id).ifPresentOrElse(
                (musicEntity) -> {
                    String youtubeLink = musicEntity.getYoutubeLink();

                    String str = youtubeLink;
                    mattermostUtil.sendWebhookChannel(str, webhookVO);
                },
                () -> {

                }
        );
    }

    @Transactional(readOnly = true)
    @Override
    public void music(WebhookVO webhookVO) {
        musicREP.findMusicRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();
                    String singer = musicEntity.getSinger();
                    String pubDate = musicEntity.getPubDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String youtubeLink = musicEntity.getYoutubeLink() != null ? "[Link]" + "(" + musicEntity.getYoutubeLink() + ")" : "-";

                    String str = title + " " + singer + " " + pubDate + " " + youtubeLink;
                    mattermostUtil.sendWebhookChannel(str, webhookVO);
                },
                () -> {

                }
        );
    }

    @jakarta.transaction.Transactional
    @Override
    public void insMusic() {
        Integer i = 84500000;

        for (Integer no = i; no > 84499950; no--) {
            try {
                Document doc = chromeDriverConnUtil.conn(URL + String.valueOf(no));
                String title = doc.getElementsByTag("main").get(0).getElementsByClass("title").get(0).ownText();
                String singer = doc.getElementsByTag("main").get(0).getElementsByClass("link_sub_title").get(0).getElementsByClass("text").get(0).ownText();
                String album = doc.getElementsByTag("main")
                        //                .get(0).getElementsByClass("end_section")
                        .get(0).getElementsByClass("album_info_area")
                        .get(0).getElementsByClass("text_area")
                        .get(0).getElementsByClass("title")
                        .get(0).ownText();
                String lyrics = doc.getElementsByTag("main")
                        .get(0).getElementsByClass("lyrics")
                        .get(0).children()
                        .get(0).ownText();
                String pubDate = doc.getElementsByTag("main")
                        .get(0).getElementsByClass("album_info_area")
                        .get(0).getElementsByClass("date").get(0).ownText();
                String[] split = pubDate.split("\\.");
                LocalDate localDate = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

//                log.info("doc : {}", doc);
//
//                log.info("title : {}", title);
//                log.info("singer : {}", singer);
//                log.info("album : {}", album);
//                log.info("lyrics : {}", lyrics);
//                log.info("pubDate : {}", localDate);
                final Integer number = no;

                musicREP.findTop1ByNoOrderByIdDesc(no.longValue()).ifPresentOrElse(
                        (v)->{
                            v.updMusic(album, title, singer, lyrics, localDate);

                            musicREP.save(v);
                        },
                        ()->{
                            musicREP.save(
                                    MusicEntity.builder()
                                            .no(number.longValue())
                                            .title(title)
                                            .singer(singer)
                                            .album(album)
                                            .lyrics(lyrics)
                                            .pubDate(localDate)
                                            .build()
                            );
                        }
                );

                log.info("done : {}", no);

            } catch (Exception e) {
                log.error("{} insMusic error", no, e);

                final Integer number = no;

                musicREP.findTop1ByNoOrderByIdDesc(no.longValue()).ifPresentOrElse(
                        (v)->{},
                        ()->{
                            musicREP.save(
                                    MusicEntity.builder()
                                            .no(number.longValue())
                                            .title("")
                                            .singer("")
                                            .build()
                            );
                        }
                );
            }
        }
    }
}
