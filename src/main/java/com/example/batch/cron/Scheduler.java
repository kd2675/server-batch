package com.example.batch.cron;

import com.example.batch.service.batch.common.CustomJobParametersIncrementer;
import com.example.batch.service.batch.job.CoinJob;
import com.example.batch.service.batch.job.HotdealJob;
import com.example.batch.service.batch.job.NewsJob;
import com.example.batch.service.batch.job.OrderJob;
import com.example.batch.service.coin.api.biz.ins.InsCoinService;
import com.example.batch.service.lotto.api.biz.LottoService;
import com.example.batch.service.reset.api.biz.Reset;
import com.example.batch.service.sport.biz.InsSportSVC;
import lombok.RequiredArgsConstructor;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    private final InsSportSVC insSportSVC;
    private final InsCoinService insCoinService;
    private final Reset reset;

    private final LottoService lottoService;


    @Scheduled(cron = "0 55 14 * * *")
    public void buyTest() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            lottoService.check();
        }
    }

    @Scheduled(cron = "0 0 10 * * SAT")
    public void buy() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            lottoService.buy();
        }
    }
    @Scheduled(cron = "0 0 22 * * SAT")
    public void check() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            lottoService.check();
        }
    }

//    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60000)+60000 }")
//    public void sport() throws Exception {
//        // add parameters as needed
//        if (ServerTypeUtils.isLocal()) {
//            insSportSVC.saveSport();
//        }
//    }

//    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60000)+60000 }")
//    public void sport68() throws Exception {
//        // add parameters as needed
//        if (ServerTypeUtils.isLocal()) {
//            insSportSVC.saveSport68();
//        }
//    }
//    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60000)+100000 }")
//    public void sport68() throws Exception {
//        // add parameters as needed
//        if (ServerTypeUtils.isLocal()) {
//            insSportSVC.saveSportJangSung911();
//        }
//    }

//    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(60000)+60000 }")
//    public void sport68Cus() throws Exception {
//        // add parameters as needed
//        if (ServerTypeUtils.isLocal()) {
//            insSportSVC.saveSport68Cus();
//        }
//    }

    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(100000)+180000 }")
    public void insHotdeal() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(HotdealJob.INS_HOTDEAL_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "0 0/5 8-23 * * *")
    @Async("asyncTaskExecutor")
    public void sendHotdeal() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(HotdealJob.SEND_HOTDEAL_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @Async("asyncTaskExecutor")
    public void delSentHotdealJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(HotdealJob.DEL_SENT_HOTDEAL_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "0 0 4 20 * *")
    @Async("asyncTaskExecutor")
    public void delHotdealJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(HotdealJob.DEL_HOTDEAL_JOB), getJobParameters());
        }
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void orderJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(OrderJob.UPD_ORDER_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "10 * 8-20 * * *")
    @Async("asyncTaskExecutor")
    public void insNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.INS_NEWS_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "10,30,50 * 8,11,13,17 * * *")
    @Async("asyncTaskExecutor")
    public void sendNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.SEND_NEWS_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "20 * * * * *")
    @Async("asyncTaskExecutor")
    public void sendNewsFlashJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.SEND_NEWS_FLASH_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "40 * 8,11,13,17 * * *")
    @Async("asyncTaskExecutor")
    public void sendNewsMarketingJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.SEND_NEWS_MARKETING_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "40 * 8,11,13,17 * * *")
    @Async("asyncTaskExecutor")
    public void sendNewsStockJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.SEND_NEWS_STOCK_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @Async("asyncTaskExecutor")
    public void delNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(NewsJob.DEL_NEWS_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "0 5,35 * * * *")
    public void reset() {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            reset.mattermostDelReset();
        }
    }

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0/1 * 8-17 * * *")
    @Async("asyncTaskExecutor")
    public void coinSaveJob() throws Exception {
        if (ServerTypeUtils.isProd()) {
            insCoinService.saveCoinDataBTC();
        }
    }

    @Scheduled(cron = "30 0 4 * * *")
    @Async("asyncTaskExecutor")
    public void coinDeleteJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(CoinJob.DEL_COIN_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "20 0,30 8-19 * * *")
    @Async("asyncTaskExecutor")
    public void sendCoinJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(CoinJob.SEND_COIN_JOB), getJobParameters());
        }
    }

    @Scheduled(cron = "20 0 20-23,0-7 * * *")
    @Async("asyncTaskExecutor")
    public void sendCoinOtherJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            jobLauncher.run(jobRegistry.getJob(CoinJob.SEND_COIN_JOB), getJobParameters());
        }
    }

    public static JobParameters getJobParameters() {
        return new JobParametersBuilder()
                .addJobParameters(new CustomJobParametersIncrementer().getNext(new JobParameters()))
                .toJobParameters();
    }

//    --------------------------------------------dev-----------------------------------------------


    @Scheduled(fixedRate = 1000 * 60 * 10)
//    @Scheduled(cron = "0/1 * 8-17 * * *")
    @Async("asyncTaskExecutor")
    public void coinSaveJobDev() throws Exception {
        if (ServerTypeUtils.isDev()) {
            insCoinService.saveCoinDataBTC();
        }
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000 * 60)
    public void orderJobDev() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isDev()) {
            jobLauncher.run(jobRegistry.getJob(OrderJob.UPD_ORDER_JOB), getJobParameters());
        }
    }

//    @Scheduled(cron = "10 0/10 9-16 ? * 1-5")
//    public void saveKospi(){
//        insKospiSVC.saveKospi();
//    }
//
//    @Scheduled(cron = "20 0 9-14 ? * 1-5")
//    public void sendMattermostKospi(){
//        sendKospiSVC.sendMattermostKospi();
//    }
//
//    @Scheduled(cron = "20 0 9,15 ? * 1-5")
//    public void sendMattermostKospiWeek(){
//        sendKospiSVC.sendMattermostKospiWeek();
//    }


//
//    @Scheduled(fixedRate = 4000)
//    public void run1Job() throws Exception {
//        String jobName = "simpleJob7";
//        JobParameters parameters = new JobParametersBuilder()
//                .addJobParameters(new CustomJobParametersIncrementer().getNext(new JobParameters()))
//                .toJobParameters();
//        // add parameters as needed
//        jobLauncher.run(jobRegistry.getJob(jobName), parameters);
//    }
}
