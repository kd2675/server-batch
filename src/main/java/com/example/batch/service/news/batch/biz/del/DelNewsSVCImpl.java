package com.example.batch.service.news.batch.biz.del;

import com.example.batch.service.batch.step.NewsStep;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsREP;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsEntity;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsREP;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
@Primary
public class DelNewsSVCImpl implements DelNewsSVC{
    private final NewsREP newsREP;
    private final OldNewsREP oldNewsREP;
    @Override
    public JpaPagingItemReader<NewsEntity> readNews(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<NewsEntity> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };

        reader.setName("jpaPagingItemReader");
        reader.setPageSize(NewsStep.PAGE_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM NewsEntity e");

        return reader;
    }

    @Override
    public ItemProcessor<NewsEntity, NewsEntity> processNews() {
        return item -> item;
    }

    @Override
    public ItemWriter<NewsEntity> copyNewsToOldNews(EntityManagerFactory entityManagerFactory) {
        return items -> {
            for (NewsEntity item : items) {
                OldNewsEntity newsEntity = OldNewsEntity.builder()
                        .category(item.getCategory())
                        .company(item.getCompany())
                        .title(item.getTitle())
                        .content(item.getContent())
                        .link(item.getLink())
                        .pubDate(item.getPubDate())
                        .build();
                oldNewsREP.save(newsEntity);
            }
        };
    }

    @Override
    public ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory) {
        return newsREP::deleteAll;
    }
}
