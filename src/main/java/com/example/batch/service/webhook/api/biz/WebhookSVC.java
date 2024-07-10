package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface WebhookSVC {
    void help();
    void time();
    void uptime();
    void news(WebhookVO webhookVO);
    void oldNews(WebhookVO webhookVO);
}