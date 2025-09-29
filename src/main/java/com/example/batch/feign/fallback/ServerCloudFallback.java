package com.example.batch.feign.fallback;


import com.example.batch.feign.client.ServerCloudClient;
import org.example.core.request.BatchExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.core.request.BatchServiceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ServerCloudFallback implements ServerCloudClient {

    @Override
    public ResponseEntity<Map<String, Object>> execute(BatchExecuteRequest request, String source) {
        log.warn("server-cloud 배치 실행 실패 - fallback 실행: {}", request.getJobType());

        Map<String, Object> response = Map.of(
                "success", false,
                "message", "server-cloud가 일시적으로 사용할 수 없습니다",
                "jobType", request.getJobType(),
                "fallback", true,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> service(BatchServiceRequest request, String source) {
        log.warn("server-cloud 배치 실행 실패 - fallback 실행: {}", request.getJobType());

        Map<String, Object> response = Map.of(
                "success", false,
                "message", "server-cloud가 일시적으로 사용할 수 없습니다",
                "jobType", request.getJobType(),
                "fallback", true,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getBatchStatus(String requestId, String detail) {
        log.warn("server-cloud 상태 조회 실패 - fallback 실행: {}", requestId);

        Map<String, Object> response = Map.of(
                "requestId", requestId,
                "status", "UNKNOWN",
                "message", "상태 조회 서비스 사용 불가",
                "fallback", true
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.warn("server-cloud health check 실패 - fallback 실행");

        Map<String, Object> response = Map.of(
                "status", "DOWN",
                "message", "Gateway 연결 불가",
                "fallback", true
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> uploadFile(MultipartFile file, String type) {
        log.warn("server-cloud 파일 업로드 실패 - fallback 실행");

        Map<String, Object> response = Map.of(
                "success", false,
                "message", "파일 업로드 서비스 사용 불가",
                "fallback", true
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAllJobs(int limit, int offset, String status) {
        log.warn("server-cloud 작업 목록 조회 실패 - fallback 실행");
        return ResponseEntity.status(503).body(List.of());
    }

    @Override
    public ResponseEntity<Map<String, Object>> cancelJob(String jobId) {
        log.warn("server-cloud 작업 취소 실패 - fallback 실행: {}", jobId);

        Map<String, Object> response = Map.of(
                "success", false,
                "jobId", jobId,
                "message", "작업 취소 서비스 사용 불가",
                "fallback", true
        );

        return ResponseEntity.status(503).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getMetrics(String service, String timeRange) {
        log.warn("server-cloud 메트릭 조회 실패 - fallback 실행");

        Map<String, Object> response = Map.of(
                "metrics", Map.of(),
                "message", "메트릭 서비스 사용 불가",
                "fallback", true
        );

        return ResponseEntity.status(503).body(response);
    }
}