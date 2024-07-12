package com.example.batch.service.music.api.biz;

import com.example.batch.service.music.database.rep.jpa.music.MusicEntity;
import com.example.batch.service.music.database.rep.jpa.music.MusicREP;
import com.example.batch.utils.ChromeDriverConnUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsMusicSVCImpl implements InsMusicSVC {
    private final String URL = "https://vibe.naver.com/track/";
    private final ChromeDriverConnUtil chromeDriverConnUtil;

    private final MusicREP musicREP;


    @Transactional
    @Override
    public void insMusic() {
        Integer i = 100;

        for (Integer no = i; no < 110; no++) {
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

                log.info("doc : {}", doc);

                log.info("title : {}", title);
                log.info("singer : {}", singer);
                log.info("album : {}", album);
                log.info("lyrics : {}", lyrics);
                log.info("pubDate : {}", localDate);

                MusicEntity musicEntity = musicREP.findTop1ByNoOrderByIdDesc(no.longValue());

                if (musicEntity != null) {
                    musicEntity.updMusic(album, title, singer, lyrics, localDate);

                    musicREP.save(musicEntity);
                } else {
                    musicREP.save(
                            MusicEntity.builder()
                                    .no(no.longValue())
                                    .title(title)
                                    .singer(singer)
                                    .album(album)
                                    .lyrics(lyrics)
                                    .pubDate(localDate)
                                    .build()
                    );
                }


            } catch (Exception e) {
                log.error("{} insMusic error", no, e);

                MusicEntity musicEntity = musicREP.findTop1ByNoOrderByIdDesc(no.longValue());

                if (musicEntity != null) {

                } else {
                    musicREP.save(
                            MusicEntity.builder()
                                    .no(no.longValue())
                                    .title("")
                                    .singer("")
                                    .build()
                    );
                }
            }
        }
    }
}
