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
    COMMAND_5(5L, "$musicSearch", "$ms", "노래검색 ex)$musicSearch '노래' 0(pageNo) 10(pagePerCnt<=10)"),
    COMMAND_6(6L, "$playlist", "$pl", "플레이리스트"),
    COMMAND_7(7L, "$playlistAdd", "$pad", "플레이리스트 노래 추가 ex)playlistAdd 321(musicSearch 번호)"),
    COMMAND_8(8L, "$playlistRemove", "$prm", "플레이리스트 노래 삭제 ex)playlistRemove 2(playlist id)"),

    COMMAND_9(9L, "$watch", "$w", "!작업중! 추천 리스트 에서 랜덤으로 영화&미드&애니 추천."),
    COMMAND_10(10L, "$watchList", "$wl", "!작업중! 추천 영화&미드&애니 리스트 ex)$watchList"),
    COMMAND_11(11L, "$watchListAdd", "$wad", "!작업중! 추천 영화&미드&애니 리스트 삭제 ex)$watchListAdd '제목' 10(평점)"),
    COMMAND_12(12L, "$watchListRemove", "$wrm", "!작업중! 추천 영화&미드&애니 리스트 삭제 ex)$watchListRemove 2(watchList id)"),

    COMMAND_13(13L, "$rockScissorsPaper", "rsp", "!작업중! 가위 바위 보"),

    COMMAND(9999L, "$command", "$c", "명령어 설명");

    private Long id;
    private String key;
    private String shortKey;
    private String value;
}
