package com.example.batch.service.batch.job;

import com.example.batch.config.JpaConfig;
import com.example.batch.config.ScheduleBatchConfig;
import com.example.batch.database.batch.BatchDataConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes={ScheduleBatchConfig.class, BatchDataConfig.class, JpaConfig.class})
@SpringBatchTest
@ActiveProfiles("test")
class CoinDeleteBatchTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @BeforeEach
    public void clearJobExecutions() {
//        this.jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void 같은조건을읽고_업데이트할때() throws Exception {
        // given
//        JobParameters jobParameters = this.jobLauncherTestUtils.getUniqueJobParametersBuilder()
//                .addJobParameters(new CustomJobParametersIncrementer().getNext(new JobParameters()))
//                .toJobParameters();

        // when
//        JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(new CustomJobParametersIncrementer().getNext(new JobParameters()));

        // then
//        assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
//        assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        //given
//        for (long i = 0; i < 50; i++) {
//            payRepository.save(new Pay(i, false));
//        }

        //when
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
//        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
//        assertThat(payRepository.findAllSuccess().size(), is(50));

    }

}