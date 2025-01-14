package com.example.batch.service.batch.writer;

import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HotdealWriter {
    public static final String JPA_ITEM_WRITER = "insHotdealWhiter";

    @Bean(name = JPA_ITEM_WRITER)
    @StepScope
    public JpaItemWriter<HotdealEntity> jpaItemWriter(@Qualifier("hotdealEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<HotdealEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
