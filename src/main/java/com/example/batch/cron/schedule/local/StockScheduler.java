package com.example.batch.cron.schedule.local;

import com.example.batch.feign.service.ServerCloudService;
import com.example.batch.kafka.producers.TestProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchServiceRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockScheduler {
    private final ServerCloudService serverCloudService;
    private final TestProducer testProducer;

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000)
    public void logCacheStats() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isLocal()) {
            BatchServiceRequest request = BatchServiceRequest.logCacheStats();

//            serverCloudService.serviceAsyncBatch(request);
            testProducer.send();
        }
    }
}
