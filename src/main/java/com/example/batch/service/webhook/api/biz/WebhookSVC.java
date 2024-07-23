package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface WebhookSVC extends NotRunSVC {
    void help(WebhookVO webhookVO);

    void time(WebhookVO webhookVO);

    void uptime(WebhookVO webhookVO);

    void news(WebhookVO webhookVO);

    void oldNews(WebhookVO webhookVO);
}