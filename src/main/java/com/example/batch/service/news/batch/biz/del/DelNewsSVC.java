package com.example.batch.service.news.batch.biz.del;

import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;

public interface DelNewsSVC {
    JpaPagingItemReader<NewsEntity> readNews(EntityManagerFactory entityManagerFactory);
    ItemProcessor<NewsEntity, NewsEntity> processNews();
    ItemWriter<NewsEntity> copyNewsToOldNews(EntityManagerFactory entityManagerFactory);
    ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory);
}
