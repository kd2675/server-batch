package com.example.batch.service.webhook.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebhookEnum {
    COMMAND_0(0L, "$time", "퇴근까지 남은 시간 알려드립니다."),
    COMMAND_1(1L, "$uptime", "출근 후 지난 시간 알려드립니다.");

    private Long id;
    private String key;
    private String value;
}
