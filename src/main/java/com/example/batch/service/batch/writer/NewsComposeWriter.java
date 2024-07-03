package com.example.batch.service.batch.writer;

import com.example.batch.service.batch.common.BasicWriter;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class NewsComposeWriter {
    public static final String SAVE_OLD_NEWS_AND_DEL_ALL_NEWS = "saveOldNewsAndDelAllNews";
    public static final String SEND_NEWS_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN = "sendNewsToMattermostAndSaveMattermostSentAndUpdSendYn";
    public static final String SEND_NEWS_FLASH_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN = "sendNewsFlashToMattermostAndSaveMattermostSentAndUpdSendYn";
    public static final String SEND_NEWS_MARKETING_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN = "sendNewsMarketingToMattermostAndSaveMattermostSentAndUpdSendYn";
    public static final String DEL_MATTERMOST_UTIL_BY_ID_AND_DEL_ALL_MATTERMOST_SENT = "delMattermostUtilByIdAndDelAllMattermostSent";

    @Bean(name = SAVE_OLD_NEWS_AND_DEL_ALL_NEWS)
    @StepScope
    public CompositeItemWriter<NewsEntity> saveOldNewsAndDelAllNews(
            @Qualifier(NewsWriter.OLD_NEWS_SAVE) BasicWriter<NewsEntity> oldNewsSave,
            @Qualifier(NewsWriter.DEL_ALL_NEWS) BasicWriter<NewsEntity> newsDelAll
    ) {
        CompositeItemWriter<NewsEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(oldNewsSave, newsDelAll));
        return compositeItemWriter;
    }

    @Bean(name = SEND_NEWS_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN)
    @StepScope
    public CompositeItemWriter<NewsEntity> sendNewsToMattermostAndSaveMattermostSentAndUpdSendYn(
            @Qualifier(MattermostWriter.SEND_NEWS_AND_SAVE_MATTERMOST_SENT) BasicWriter<NewsEntity> itemSender,
            @Qualifier(NewsWriter.JPA_ITEM_WRITER) JpaItemWriter<NewsEntity> itemWriter
    ) {
        CompositeItemWriter<NewsEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(itemSender, itemWriter));
        return compositeItemWriter;
    }

    @Bean(name = SEND_NEWS_FLASH_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN)
    @StepScope
    public CompositeItemWriter<NewsEntity> sendNewsFlashToMattermostAndSaveMattermostSentAndUpdSendYn(
            @Qualifier(MattermostWriter.SEND_NEWS_FLASH_AND_SAVE_MATTERMOST_SENT) BasicWriter<NewsEntity> itemSender,
            @Qualifier(NewsWriter.JPA_ITEM_WRITER) JpaItemWriter<NewsEntity> itemWriter
    ) {
        CompositeItemWriter<NewsEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(itemSender, itemWriter));
        return compositeItemWriter;
    }

    @Bean(name = SEND_NEWS_MARKETING_TO_MATTERMOST_AND_SAVE_MATTERMOST_SENT_AND_UPD_SEND_YN)
    @StepScope
    public CompositeItemWriter<NewsEntity> sendNewsMarketingToMattermostAndSaveMattermostSentAndUpdSendYn(
            @Qualifier(MattermostWriter.SEND_NEWS_MARKETING_AND_SAVE_MATTERMOST_SENT) BasicWriter<NewsEntity> itemSender,
            @Qualifier(NewsWriter.JPA_ITEM_WRITER) JpaItemWriter<NewsEntity> itemWriter
    ) {
        CompositeItemWriter<NewsEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(itemSender, itemWriter));
        return compositeItemWriter;
    }

    @Bean(name = DEL_MATTERMOST_UTIL_BY_ID_AND_DEL_ALL_MATTERMOST_SENT)
    @StepScope
    public CompositeItemWriter<MattermostSentEntity> delMattermostUtilByIdAndDelAllMattermostSent(
            @Qualifier(MattermostWriter.DEL_MATTERMOST_UTIL_BY_ID) ItemWriter<MattermostSentEntity> itemCopier,
            @Qualifier(MattermostWriter.DEL_ALL_MATTERMOST_SENT) ItemWriter<MattermostSentEntity> itemWriter
    ) {
        CompositeItemWriter<MattermostSentEntity> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(Arrays.asList(itemCopier, itemWriter));
        return compositeItemWriter;
    }
}
