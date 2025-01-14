package com.example.batch.service.batch.writer;

import com.example.batch.service.batch.common.BasicWriter;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class HotdealComposeWriter {
    public static final String HOTDEAL_MATTERMOST_SEND_AND_UPD_SEND_YN = "hotdealMattermostSendAndUpdSendYn";

    @Bean(name = HOTDEAL_MATTERMOST_SEND_AND_UPD_SEND_YN)
    @StepScope
    public CompositeItemWriter<HotdealEntity> sendNewsMarketingToMattermostAndSaveMattermostSentAndUpdSendYn(
            @Qualifier(MattermostWriter.SEND_HOTDEAL_AND_SAVE_MATTERMOST_SENT) BasicWriter<HotdealEntity> itemSender,
            @Qualifier(HotdealWriter.JPA_ITEM_WRITER) JpaItemWriter<HotdealEntity> itemWriter
    ) {
        CompositeItemWriter<HotdealEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(itemSender, itemWriter));
        return compositeItemWriter;
    }
}
