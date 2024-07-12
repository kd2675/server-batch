package com.example.batch.service.webhook.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebhookEnum {
    COMMAND_0(0L, "$time", "퇴근까지 남은 시간 알려드립니다."),
    COMMAND_1(1L, "$uptime", "출근 후 지난 시간 알려드립니다."),
    COMMAND_2(2L, "$news", "뉴스 찾아드립니다. ex)$news 밥,세일,탕수육(and조건) 0(pageNo) 10(pagePerCnt<=10)"),
    COMMAND_3(3L, "$oldnews", "지난 뉴스 찾아드립니다. ex)$news 밥,세일,탕수육(and조건) 0(pageNo) 10(pagePerCnt<=10)"),
    COMMAND_4(4L, "$music", "랜덤으로 노래 추천.");

    private Long id;
    private String key;
    private String value;
}
