package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchExecuteRequest;
import org.example.core.request.BatchServiceRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoinScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0/1 * 8-17 * * *")
    public void coinSaveJob() throws Exception {
        if (ServerTypeUtils.isProd()) {
            serverCloudService.service(BatchServiceRequest.saveCoinDataBTC());
        }
    }

    @Scheduled(cron = "30 0 4 * * *")
    public void coinDeleteJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsync(BatchExecuteRequest.delCoinJob());
        }
    }

    @Scheduled(cron = "20 0,30 8-19 * * *")
    public void sendCoinJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendCoinJob());
        }
    }

    @Scheduled(cron = "20 0 20-23,0-7 * * *")
    public void sendCoinOtherJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendCoinJob());
        }
    }
}
