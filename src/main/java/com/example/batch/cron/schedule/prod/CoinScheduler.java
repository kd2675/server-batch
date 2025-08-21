package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchExecuteRequest;
import org.example.core.request.BatchServiceRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoinScheduler {


    private final ServerCloudService serverCloudService;

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0/1 * 8-17 * * *")
    @Async("asyncTaskExecutor")
    public void coinSaveJob() throws Exception {
        if (ServerTypeUtils.isProd()) {
            serverCloudService.serviceAsyncBatch(BatchServiceRequest.saveCoinDataBTC());
        }
    }

    @Scheduled(cron = "30 0 4 * * *")
    @Async("asyncTaskExecutor")
    public void coinDeleteJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.delCoinJob());
        }
    }

    @Scheduled(cron = "20 0,30 8-19 * * *")
    @Async("asyncTaskExecutor")
    public void sendCoinJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.sendCoinJob());
        }
    }

    @Scheduled(cron = "20 0 20-23,0-7 * * *")
    @Async("asyncTaskExecutor")
    public void sendCoinOtherJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.sendCoinJob());
        }
    }
}
