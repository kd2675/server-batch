package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface NotRunSVC {
    void notRun(WebhookVO webhookVO);
}
