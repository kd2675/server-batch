package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchServiceRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LottoScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(cron = "0 0 18 * * FRI")
    public void orderCheck() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.account();

            serverCloudService.serviceAsync(request);
        }
    }

    @Scheduled(cron = "0 0 10 * * SAT")
    public void buy() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.buy();

            serverCloudService.serviceAsync(request);
        }
    }

    @Scheduled(cron = "0 0 22 * * SAT")
    public void check() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.check();

            serverCloudService.serviceAsync(request);
        }
    }
}
