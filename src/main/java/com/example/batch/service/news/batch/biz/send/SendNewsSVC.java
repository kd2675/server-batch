package com.example.batch.service.news.batch.biz.send;

import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;

public interface SendNewsSVC {
    ListItemReader<NewsEntity> readNews(EntityManagerFactory entityManagerFactory);
    ItemProcessor<NewsEntity, NewsEntity> processNews();
    ItemWriter<NewsEntity> sendNews(EntityManagerFactory entityManagerFactory);
    ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory);
}
