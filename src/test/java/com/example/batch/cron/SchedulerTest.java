package com.example.batch.cron;

import org.example.core.request.BatchExecuteRequest;
import com.example.batch.feign.service.ServerCloudService;
import org.example.core.utils.ServerTypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SchedulerTest {
    @Mock
    private ServerCloudService serverCloudService;

    @InjectMocks
    private Scheduler scheduler; // 실제 서비스 클래스명으로 변경

    @BeforeEach
    void setUp() {
        // 테스트 초기화 로직
    }


    /**
     * 테스트 1: Local 환경에서 정상 실행
     */
    @Test
    void test_Local환경에서_정상실행() throws Exception {
        // Given
        MockedStatic<ServerTypeUtils> mockedStatic = mockStatic(ServerTypeUtils.class);
        mockedStatic.when(ServerTypeUtils::isLocal).thenReturn(true);

        // When
        scheduler.test();

        // Then
        verify(serverCloudService, times(1))
                .executeAsyncBatch(eq("HOTDEAL"), any(BatchExecuteRequest.class));
    }

}