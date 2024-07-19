package com.example.batch.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelEnum {

    MATTERMOST_CHANNEL_BOB(5L, "bob", "ajin5qs41b8y5ksyu61zq54f7w"),

    MATTERMOST_CHANNEL_COIN(0L, "coin", "947q6tnbc3gw9k9uwyxtboqx5h"),
    MATTERMOST_CHANNEL_NEWS(1L, "news", "sph9p8g1uiygindx7qh8tnxmgr"),
    MATTERMOST_CHANNEL_NEWS_FLASH(2L, "news", "8k97zj11hiyiic54xtbc3sieho"),
    MATTERMOST_CHANNEL_NEWS_MARKETING(3L, "news", "5hy139iizpg33decuwtcpyjm3c"),
    MATTERMOST_CHANNEL_NEWS_STOCK(4L, "news", "6w3xkrc3c7go7jp9q44uio9i4c");

    private Long id;
    private String key;
    private String value;
}
