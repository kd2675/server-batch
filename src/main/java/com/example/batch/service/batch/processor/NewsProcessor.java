package com.example.batch.service.batch.processor;

import com.example.batch.service.batch.common.BasicProcessor;
import com.example.batch.service.news.api.vo.NaverNewsApiItemVO;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class NewsProcessor {
    public static final String NEWS_ENTITY_UPD_SEND_YN_Y = "newsEntityUpdSendYnToY";
    public static final String NAVER_NEWS_API_ITEM_VO_TO_NEWS_ENTITY = "naverNewsApiItemVoToNewsEntity";
    @Bean(name = NAVER_NEWS_API_ITEM_VO_TO_NEWS_ENTITY)
    @StepScope
    public BasicProcessor<NaverNewsApiItemVO, NewsEntity> naverNewsApiItemVoToNewsEntity() {
        return new BasicProcessor<NaverNewsApiItemVO, NewsEntity>() {
            @Override
            public NewsEntity process(NaverNewsApiItemVO item) throws Exception {
                NewsEntity newsEntity = NewsEntity.builder()
                        .category(item.getCategory())
                        .company("naverApi")
                        .title(item.getTitle())
                        .content(item.getDescription())
                        .link(item.getLink())
                        .pubDate(LocalDateTime.parse(item.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME))
                        .build();

                return newsEntity;
            }
        };
    }
    @Bean(name = NEWS_ENTITY_UPD_SEND_YN_Y)
    @StepScope
    public BasicProcessor<NewsEntity, NewsEntity> itemProcessor() {
        return new BasicProcessor<NewsEntity, NewsEntity>() {
            @Override
            public NewsEntity process(NewsEntity item) throws Exception {
                item.updSendYn("y");
                return item;
            }
        };
    }
}
