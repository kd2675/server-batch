package com.example.batch.service.batch.step;

import com.example.batch.service.batch.common.BasicProcessor;
import com.example.batch.service.batch.processor.HotdealProcessor;
import com.example.batch.service.batch.reader.HotdealReader;
import com.example.batch.service.batch.writer.HotdealComposeWriter;
import com.example.batch.service.batch.writer.HotdealWriter;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealDTO;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HotdealStep {
    public static final int CHUNK_SIZE = 100;
    public static final int PAGE_SIZE = 100;
    public static final String INS_HOTDEAL_STEP = "insHotdealStep";
    public static final String SEND_HOTDEAL_STEP = "sendHotdealStep";

    @Bean(name = INS_HOTDEAL_STEP)
    @JobScope
    public Step insHotdealStep(
            JobRepository jobRepository,
            @Qualifier("hotdealTransactionManager") PlatformTransactionManager platformTransactionManager,
            @Qualifier(HotdealReader.FIND_HOTDEAL) ListItemReader<HotdealDTO> itemReader,
            @Qualifier(HotdealProcessor.INS_HOTDEAL_PROCESSOR) BasicProcessor<HotdealDTO, HotdealEntity> itemProcessor,
            @Qualifier(HotdealWriter.JPA_ITEM_WRITER) JpaItemWriter<HotdealEntity> itemWriter
    ) {
        return new StepBuilder(INS_HOTDEAL_STEP, jobRepository)
                .<HotdealDTO, HotdealEntity>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
//                .allowStartIfComplete(true)
                .build();
    }

    @Bean(name = SEND_HOTDEAL_STEP)
    @JobScope
    public Step sendHotdealStep(
            JobRepository jobRepository,
            @Qualifier("hotdealTransactionManager") PlatformTransactionManager platformTransactionManager,
            @Qualifier(HotdealReader.FIND_ALL_HOTDEAL_SEND_YN_N) ListItemReader<HotdealEntity> itemReader,
            @Qualifier(HotdealProcessor.UPD_HOTDEAL_SEND_YN_Y) BasicProcessor<HotdealEntity, HotdealEntity> itemProcessor,
            @Qualifier(HotdealComposeWriter.HOTDEAL_MATTERMOST_SEND_AND_UPD_SEND_YN) CompositeItemWriter<HotdealEntity> itemWriter
    ) {
        return new StepBuilder(SEND_HOTDEAL_STEP, jobRepository)
                .<HotdealEntity, HotdealEntity>chunk(15, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
//                .allowStartIfComplete(true)
                .build();
    }
}
