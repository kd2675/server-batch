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
public class NewsScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(cron = "10 * * * * *")
    public void insNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.insNewsJob());
        }
    }

    @Scheduled(cron = "10,30,50 * 8,9,11,13,17 * * *")
    public void sendNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendNewsJob());
        }
    }

    @Scheduled(cron = "20 * * * * *")
    public void sendNewsFlashJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendNewsFlashJob());
        }
    }

    @Scheduled(cron = "40 * 8,11,13,17 * * *")
    public void sendNewsMarketingJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendNewsMarketingJob());
        }
    }

    @Scheduled(cron = "40 * 8,11,13,17 * * *")
    public void sendNewsStockJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.execute(BatchExecuteRequest.sendNewsStockJob());
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void delNewsJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsync(BatchExecuteRequest.delNewsJob());
        }
    }

    @Scheduled(cron = "0 5,35 * * * *")
    public void reset() {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.serviceAsync(BatchServiceRequest.reset());
        }
    }
}
