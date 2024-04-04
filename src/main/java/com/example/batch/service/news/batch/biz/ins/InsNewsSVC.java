package com.example.batch.service.news.batch.biz.ins;

import com.example.batch.service.news.api.vo.NaverNewsApiItemVO;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

public interface InsNewsSVC {
    void saveNews();
    ItemReader<NaverNewsApiItemVO> readNews();
    ItemProcessor<NaverNewsApiItemVO, NewsEntity> processNews();
    ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory);
}
