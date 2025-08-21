package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchExecuteRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void orderJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.updOrderJob());
        }
    }
}
