package com.example.batch.cron.schedule.prod;

import com.example.batch.feign.service.ServerCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchExecuteRequest;
import org.example.core.utils.ServerTypeUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class HotdealScheduler {
    private final ServerCloudService serverCloudService;

    @Scheduled(fixedRateString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(600000)+300000 }")
    public void insHotdeal() throws Exception {
        // 현재 시간 가져오기
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(7, 0); // 07:00
        LocalTime end = LocalTime.of(23, 0); // 23:00

        // 현재 시간이 7시에서 23시 사이인지 확인
        if (!now.isBefore(start) && !now.isAfter(end)) {
            // add parameters as needed
            if (ServerTypeUtils.isProd()) {
                serverCloudService.executeAsyncBatch(BatchExecuteRequest.insHotdealJob());
            }
        }
    }

    @Scheduled(cron = "0 0/5 8-23 * * *")
    @Async("asyncTaskExecutor")
    public void sendHotdeal() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.sendHotdealJob());
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @Async("asyncTaskExecutor")
    public void delSentHotdealJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.delSentHotdealJob());
        }
    }

    @Scheduled(cron = "0 0 4 20 * *")
    @Async("asyncTaskExecutor")
    public void delHotdealJob() throws Exception {
        // add parameters as needed
        if (ServerTypeUtils.isProd()) {
            serverCloudService.executeAsyncBatch(BatchExecuteRequest.delHotdealJob());
        }
    }
}
