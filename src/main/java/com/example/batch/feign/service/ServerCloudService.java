package com.example.batch.feign.service;

import com.example.batch.feign.client.ServerCloudClient;
import org.example.core.request.BatchExecuteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchServiceRequest;
import org.example.core.request.Priority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServerCloudService {
    private final ServerCloudClient serverCloudClient;

    /**
     * 비동기 배치 실행
     */
    public void executeAsync(BatchExecuteRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                var response = serverCloudClient.executeAsync(request, "server-batch");

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = response.getBody();
                    log.info("Gateway를 통한 {} 배치 실행 성공: {}", request.getJobType(), result);

//                    // 요청 ID가 있으면 상태 추적 시작
//                    if (result != null && result.containsKey("requestId")) {
//                        String requestId = (String) result.get("requestId");
//                        trackBatchStatus(requestId, request.getJobType());
//                    }

                } else {
                    log.error("Gateway를 통한 {} 배치 실행 실패: {}", request.getJobType(), response.getStatusCode());
                }

            } catch (Exception e) {
                log.error("Gateway를 통한 {} 배치 실행 중 오류 발생", request.getJobType(), e);
            }
        });
    }

    public void execute(BatchExecuteRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                var response = serverCloudClient.execute(request, "server-batch");

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = response.getBody();
                    log.info("Gateway를 통한 {} 배치 실행 성공: {}", request.getJobType(), result);

//                    // 요청 ID가 있으면 상태 추적 시작
//                    if (result != null && result.containsKey("requestId")) {
//                        String requestId = (String) result.get("requestId");
//                        trackBatchStatus(requestId, request.getJobType());
//                    }

                } else {
                    log.error("Gateway를 통한 {} 배치 실행 실패: {}", request.getJobType(), response.getStatusCode());
                }

            } catch (Exception e) {
                log.error("Gateway를 통한 {} 배치 실행 중 오류 발생", request.getJobType(), e);
            }
        });
    }

    public void serviceAsync(BatchServiceRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                var response = serverCloudClient.serviceAsync(request, "server-batch");

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = response.getBody();
                    log.info("Gateway를 통한 {} 배치 실행 성공: {}", request.getJobType(), result);

//                    // 요청 ID가 있으면 상태 추적 시작
//                    if (result != null && result.containsKey("requestId")) {
//                        String requestId = (String) result.get("requestId");
//                        trackBatchStatus(requestId, request.getJobType());
//                    }

                } else {
                    log.error("Gateway를 통한 {} 배치 실행 실패: {}", request.getJobType(), response.getStatusCode());
                }

            } catch (Exception e) {
                log.error("Gateway를 통한 {} 배치 실행 중 오류 발생", request.getJobType(), e);
            }
        });
    }

    public void service(BatchServiceRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                var response = serverCloudClient.service(request, "server-batch");

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = response.getBody();
                    log.info("Gateway를 통한 {} 배치 실행 성공: {}", request.getJobType(), result);

//                    // 요청 ID가 있으면 상태 추적 시작
//                    if (result != null && result.containsKey("requestId")) {
//                        String requestId = (String) result.get("requestId");
//                        trackBatchStatus(requestId, request.getJobType());
//                    }

                } else {
                    log.error("Gateway를 통한 {} 배치 실행 실패: {}", request.getJobType(), response.getStatusCode());
                }

            } catch (Exception e) {
                log.error("Gateway를 통한 {} 배치 실행 중 오류 발생", request.getJobType(), e);
            }
        });
    }

    /**
     * 배치 상태 추적
     */
    private void trackBatchStatus(String requestId, String jobType) {
        CompletableFuture.runAsync(() -> {
            int maxAttempts = 10;
            int attempt = 0;

            while (attempt < maxAttempts) {
                try {
                    Thread.sleep(30000); // 30초 대기

                    var response = serverCloudClient.getBatchStatus(requestId, "full");

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Map<String, Object> status = response.getBody();
                        if (status != null) {
                            String currentStatus = (String) status.get("status");
                            log.info("{} 배치 상태 ({}): {}", jobType, requestId, currentStatus);

                            if ("COMPLETED".equals(currentStatus) || "FAILED".equals(currentStatus)) {
                                break;
                            }
                        }
                    }

                    attempt++;

                } catch (Exception e) {
                    log.error("배치 상태 추적 중 오류: {}", requestId, e);
                    break;
                }
            }
        });
    }

    /**
     * 장시간 실행 작업 체크
     */
    private boolean isLongRunningJob(Map<String, Object> job) {
        try {
            String startTimeStr = (String) job.get("startTime");
            if (startTimeStr != null) {
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
                return LocalDateTime.now().minusMinutes(5).isAfter(startTime);
            }
        } catch (Exception e) {
            log.debug("작업 시작 시간 파싱 실패", e);
        }
        return false;
    }

    /**
     * 수동 배치 실행 (테스트용)
     */
    public String executeManualBatch(String jobType, Map<String, Object> parameters) {
        try {
            BatchExecuteRequest request = BatchExecuteRequest.builder()
                    .jobType(jobType)
                    .source("health")
                    .priority(Priority.LOW)
                    .parameters(parameters)
                    .requestTime(LocalDateTime.now())
                    .build();

            var response = serverCloudClient.execute(request, "server-batch");

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                return (String) result.get("requestId");
            }

        } catch (Exception e) {
            log.error("수동 배치 실행 실패", e);
        }

        return null;
    }
}
