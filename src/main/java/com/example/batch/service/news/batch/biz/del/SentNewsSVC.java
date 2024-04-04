package com.example.batch.service.news.batch.biz.del;

import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;

public interface SentNewsSVC {
    JpaPagingItemReader<MattermostSentEntity> readNews(EntityManagerFactory entityManagerFactory);
    ItemProcessor<MattermostSentEntity, MattermostSentEntity> processNews();
    ItemWriter<MattermostSentEntity> sentNews(EntityManagerFactory entityManagerFactory);
    ItemWriter<MattermostSentEntity> writeNews(EntityManagerFactory entityManagerFactory);
}
