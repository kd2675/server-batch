package com.example.batch.service.batch.reader;

import com.example.batch.service.batch.common.DelJpaPagingItemReader;
import com.example.batch.service.batch.step.NewsStep;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MattermostReader {
    //    public static final String FIND_ALL_MATTERMOST_SENT_FIX_PAGE_0 = "findAllMattermostSentFixPage0";
    private static final int PAGE_SIZE = 100;
    public static final String FIND_BY_CATEGORY_IS_NEWS = "findByCategoryIsNews";
    public static final String FIND_BY_CATEGORY_IS_COIN = "findByCategoryIsCoin";

    @Bean(name = FIND_BY_CATEGORY_IS_NEWS, destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<MattermostSentEntity> findByCategoryIsNews(@Qualifier("mattermostEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<MattermostSentEntity> reader = new DelJpaPagingItemReader<>();

        reader.setName("jpaPagingItemReader");
        reader.setPageSize(PAGE_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM MattermostSentEntity e WHERE e.createDate < :date and e.category = 'news' order by e.createDate");

        HashMap<String, Object> param = new HashMap<>();
        param.put("date", LocalDateTime.now().minusHours(3));
        reader.setParameterValues(param);
        return reader;
    }

    @Bean(name = FIND_BY_CATEGORY_IS_COIN, destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<MattermostSentEntity> findByCategoryIsCoin(@Qualifier("mattermostEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<MattermostSentEntity> reader = new DelJpaPagingItemReader<>();

        reader.setName("jpaPagingItemReader");
        reader.setPageSize(PAGE_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM MattermostSentEntity e WHERE e.createDate < :date and e.category = 'coin'");

        HashMap<String, Object> param = new HashMap<>();
        param.put("date", LocalDateTime.now().minusHours(1));
        reader.setParameterValues(param);
        return reader;
    }
}
