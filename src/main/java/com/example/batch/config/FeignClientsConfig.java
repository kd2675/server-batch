package com.example.batch.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class FeignClientsConfig {
    @Bean
    public RequestInterceptor serverCloudRequestInterceptor() {
        return requestTemplate -> {
            // 공통 헤더 추가
            requestTemplate.header("User-Agent", "server-batch/1.0");
            requestTemplate.header("X-Source", "server-batch");
            requestTemplate.header("X-Request-ID", generateRequestId());
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Auth-header", "cloud");
        };
    }

    @Bean
    public ErrorDecoder serverCloudErrorDecoder() {
        return new ServerCloudErrorDecoder();
    }

    @Bean
    public Retryer serverCloudRetryer() {
        // server-cloud는 Gateway이므로 빠른 재시도
        return new Retryer.Default(500, 2000, 3);
    }

    @Bean
    public Request.Options serverCloudRequestOptions() {
        // Gateway 특성상 빠른 응답 기대
        return new Request.Options(5000, TimeUnit.MILLISECONDS, 15000, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Logger.Level serverCloudLoggerLevel() {
        return Logger.Level.HEADERS; // 헤더 포함 로깅
    }

    private String generateRequestId() {
        return "batch-" + System.currentTimeMillis();
    }

}

@Slf4j
class ServerCloudErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        String message = String.format("server-cloud 호출 실패 - HTTP %s calling %s", response.status(), methodKey);

        switch (response.status()) {
            case 400:
                log.error("server-cloud Bad Request: {}", message);
                return new IllegalArgumentException("잘못된 요청 - " + message);

            case 401:
                log.error("server-cloud Unauthorized: {}", message);
                return new SecurityException("인증 실패 - " + message);

            case 403:
                log.error("server-cloud Forbidden: {}", message);
                return new SecurityException("권한 없음 - " + message);

            case 404:
                log.error("server-cloud Not Found: {}", message);
                return new IllegalStateException("리소스 없음 - " + message);

            case 429:
                log.error("server-cloud Rate Limited: {}", message);
                return new RuntimeException("요청 제한 초과 - " + message);

            case 500:
                log.error("server-cloud Internal Server Error: {}", message);
                return new RuntimeException("Gateway 내부 오류 - " + message);

            case 502:
                log.error("server-cloud Bad Gateway: {}", message);
                return new RuntimeException("Gateway 오류 - " + message);

            case 503:
                log.error("server-cloud Service Unavailable: {}", message);
                return new RuntimeException("Gateway 서비스 불가 - " + message);

            case 504:
                log.error("server-cloud Gateway Timeout: {}", message);
                return new RuntimeException("Gateway 타임아웃 - " + message);

            default:
                log.error("server-cloud Unknown Error: {}", message);
                return new RuntimeException("알 수 없는 Gateway 오류 - " + message);
        }
    }
}

