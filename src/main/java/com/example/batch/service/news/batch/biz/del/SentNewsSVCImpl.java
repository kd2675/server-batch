package com.example.batch.service.news.batch.biz.del;

import com.example.batch.service.batch.step.NewsStep;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentEntity;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentREP;
import com.example.batch.utils.MattermostUtil;
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
public class SentNewsSVCImpl implements SentNewsSVC{
    private final MattermostSentREP mattermostSentREP;

    private final MattermostUtil mattermostUtil;
    @Override
    public JpaPagingItemReader<MattermostSentEntity> readNews(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<MattermostSentEntity> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };

        reader.setName("jpaPagingItemReader");
        reader.setPageSize(NewsStep.PAGE_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM MattermostSentEntity e");

        return reader;
    }

    @Override
    public ItemProcessor<MattermostSentEntity, MattermostSentEntity> processNews() {
        return item -> item;
    }

    @Override
    public ItemWriter<MattermostSentEntity> sentNews(EntityManagerFactory entityManagerFactory) {
        return chunk -> chunk.forEach(v -> mattermostUtil.delete(v.getSentId()));
    }

    @Override
    public ItemWriter<MattermostSentEntity> writeNews(EntityManagerFactory entityManagerFactory) {
        return mattermostSentREP::deleteAll;
    }
}
