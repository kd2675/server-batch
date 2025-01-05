package com.example.batch.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelEnum {

    MATTERMOST_CHANNEL_BOB(5L, "bob", "jsz1cxtjwfb63ccx35bpt9c3nh"),
    MATTERMOST_CHANNEL_BOT(5L, "bob", "pdxfh1a7jfg1dm8x4pbdyzrhta"),

    MATTERMOST_CHANNEL_COIN(0L, "coin", "49te3so5pirzubzrwis4h39uya"),
    MATTERMOST_CHANNEL_NEWS(1L, "news", "7ah4awp48fd9dfqxs87tia4h6c"),
    MATTERMOST_CHANNEL_NEWS_FLASH(2L, "news", "y5g4ki1ypbbezk97rwuwqnpiga"),
    MATTERMOST_CHANNEL_NEWS_MARKETING(3L, "news", "mnkeeidfgbbexf5zkmpthqhzza"),
    MATTERMOST_CHANNEL_NEWS_STOCK(4L, "news", "q3outnqxs3gidxrwzk1y6z7ika");

    private Long id;
    private String key;
    private String value;
}
