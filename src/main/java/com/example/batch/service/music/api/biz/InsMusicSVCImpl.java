package com.example.batch.service.music.api.biz;

import com.example.batch.utils.ChromeDriverConnUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsMusicSVCImpl implements InsMusicSVC{
    private final String URL = "https://vibe.naver.com/track/85264264";
    private final ChromeDriverConnUtil chromeDriverConnUtil;

    @Transactional
    @Override
    public void insMusic(){
        Document doc = chromeDriverConnUtil.conn(URL);
        String title = doc.getElementsByTag("main").get(0).getElementsByClass("title").get(0).ownText();
        String singer = doc.getElementsByTag("main").get(0).getElementsByClass("link_sub_title").get(0).getElementsByClass("text").get(0).ownText();
        String album = doc.getElementsByTag("main").get(0).getElementsByClass("end_section").get(0).getElementsByClass("title").get(0).ownText();
        String lyrics = doc.getElementsByTag("main").get(0).child(2).child(1).child(0).text();
        String pubDate = doc.getElementsByTag("main").get(0).getElementsByClass("end_section").get(0).getElementsByClass("date").get(0).ownText();

        log.warn("title : {}", title);
        log.warn("singer : {}", singer);
        log.warn("album : {}", album);
        log.warn("lyrics : {}", lyrics);
        log.warn("pubDate : {}", pubDate);
    }
}
