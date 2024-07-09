package com.example.batch.service.webhook.api.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WebhookVO {
    private String channelId;
    private String channelName;
    private String teamDomain;
    private String teamId;
    private String postId;
    private String text;
    private String timestamp;
    private String token;
    private String triggerWord;
    private String userId;
    private String userName;
}
