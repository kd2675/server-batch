package com.example.batch.service.webhook.api.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WebhookVO {
    private String text;
    private String responseType;
    private String username;
    private String iconUrl;
    private String attachments;
    private String type;
    private String props;
    private String priority;
}
