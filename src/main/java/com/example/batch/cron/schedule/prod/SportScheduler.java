package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchExecuteRequest;
import org.example.core.request.BatchRequest;
import org.example.core.request.BatchServiceRequest;
import org.example.core.request.Priority;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class SportScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(cron = "0 50 16 29 9 *")
    public void sportService() throws Exception {
        // add parameters as needed
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", "2025");
        parameters.put("month", "10");
        parameters.put("day", "1");
        parameters.put("st", "14");

        BatchServiceRequest batchServiceRequest = BatchServiceRequest.builder()
                .jobType("beforeCheckJangsung")
                .source("server-batch")
                .priority(Priority.MEDIUM)
                .parameters(parameters)
                .requestTime(LocalDateTime.now())
                .build();


        if (ServerTypeUtils.isProd()) {
            serverCloudService.serviceAsync(batchServiceRequest);
        }
    }
}
