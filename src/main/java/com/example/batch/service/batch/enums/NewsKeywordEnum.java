package com.example.batch.service.batch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum NewsKeywordEnum {
    NEWS_KEYWORD_0(0L, "속보"),
    NEWS_KEYWORD_1(1L, "ai"),
    NEWS_KEYWORD_2(2L, "주식"),
    NEWS_KEYWORD_3(3L, "코인"),
    NEWS_KEYWORD_4(4L, "할인"),
    NEWS_KEYWORD_5(5L, "특가"),
    NEWS_KEYWORD_6(6L, "세일"),
    NEWS_KEYWORD_7(7L, "이"),
    NEWS_KEYWORD_8(8L, "가"),
    NEWS_KEYWORD_9(9L, "다"),
    NEWS_KEYWORD_10(10L, "는"),
    NEWS_KEYWORD_11(11L, "을"),
    NEWS_KEYWORD_12(12L, "고"),
    NEWS_KEYWORD_13(13L, "하"),
    NEWS_KEYWORD_14(14L, "에");

    private Long id;
    private String value;

    public static List<String> getNewsKeywordValue() {
        return Arrays.stream(NewsKeywordEnum.values())
                .map(NewsKeywordEnum::getValue)
                .filter(value ->
                        !value.equals("속보")
                                && !value.equals("할인")
                                && !value.equals("특가")
                                && !value.equals("세일")
                )
                .toList();
    }
}
