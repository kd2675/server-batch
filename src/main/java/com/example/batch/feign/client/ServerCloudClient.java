package com.example.batch.feign.client;

import com.example.batch.config.FeignClientsConfig;
import org.example.core.request.BatchExecuteRequest;
import com.example.batch.feign.fallback.ServerCloudFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "server-cloud",
        url = "http://localhost:20200/service/batch", // server-cloud 주소
        fallback = ServerCloudFallback.class,
        configuration = FeignClientsConfig.class
)
public interface ServerCloudClient {

    /**
     * Gateway를 통한 배치 실행
     */
    @PostMapping("/api/gateway/execute")
    ResponseEntity<Map<String, Object>> executeBatch(
            @RequestBody BatchExecuteRequest request,
            @RequestHeader(value = "X-Source", defaultValue = "server-batch") String source
    );

    /**
     * Gateway를 통한 배치 상태 조회
     */
    @GetMapping("/api/gateway/status/{requestId}")
    ResponseEntity<Map<String, Object>> getBatchStatus(
            @PathVariable String requestId,
            @RequestParam(required = false) String detail
    );

    /**
     * Gateway Health Check
     */
    @GetMapping("/api/gateway/health")
    ResponseEntity<Map<String, Object>> healthCheck();

    /**
     * Gateway를 통한 파일 업로드
     */
    @PostMapping(value = "/api/gateway/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Map<String, Object>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String type
    );

    /**
     * Gateway를 통한 작업 목록 조회
     */
    @GetMapping("/api/gateway/jobs")
    ResponseEntity<List<Map<String, Object>>> getAllJobs(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String status
    );

    /**
     * Gateway를 통한 작업 취소
     */
    @DeleteMapping("/api/gateway/jobs/{jobId}")
    ResponseEntity<Map<String, Object>> cancelJob(@PathVariable String jobId);

    /**
     * Gateway를 통한 메트릭 조회
     */
    @GetMapping("/api/gateway/metrics")
    ResponseEntity<Map<String, Object>> getMetrics(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String timeRange
    );
}
