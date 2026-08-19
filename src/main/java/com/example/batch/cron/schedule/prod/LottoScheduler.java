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

    @Scheduled(cron = "0 55 17 * * FRI", zone = "Asia/Seoul")
    public void orderCheck() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.account();

            serverCloudService.service(request);
        }
    }

    @Scheduled(cron = "0 0 10 * * WED", zone = "Asia/Seoul")
    public void buy() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.buy();

            serverCloudService.service(request);
        }
    }

    @Scheduled(cron = "0 0 10 * * SUN", zone = "Asia/Seoul")
    public void check() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            BatchServiceRequest request = BatchServiceRequest.check();

            serverCloudService.service(request);
        }
    }
}
