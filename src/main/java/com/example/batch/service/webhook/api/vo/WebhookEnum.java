package com.example.batch.service.webhook.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebhookEnum {
    COMMAND_0(0L, "$time", "$t", "퇴근까지 남은 시간 알려드립니다."),
    COMMAND_1(1L, "$uptime", "$ut", "출근 후 지난 시간 알려드립니다."),
    COMMAND_2(2L, "$news", "$n", "뉴스 찾아드립니다. ex)$news 밥,세일,탕수육(and조건) 0(pageNo) 10(pagePerCnt<=10)"),
    COMMAND_3(3L, "$oldNews", "$on", "지난 뉴스 찾아드립니다. ex)$oldNews 밥,세일,탕수육(and조건) 0(pageNo) 10(pagePerCnt<=10)"),
    COMMAND_4(4L, "$music", "$m", "전체 리스트 에서 랜덤으로 노래 추천."),
    COMMAND_5(5L, "$searchMusic", "$sm", "노래검색 ex)$searchMusic '노래' 0(pageNo) 10(pagePerCnt<=10)");

    private Long id;
    private String key;
    private String shortKey;
    private String value;
}
