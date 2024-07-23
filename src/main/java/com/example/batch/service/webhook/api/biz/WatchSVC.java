package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface WatchSVC extends NotRunSVC{
    void watch(WebhookVO webhookVO);

    void watchList(WebhookVO webhookVO);

    void watchAdd(WebhookVO webhookVO);

    void watchY(WebhookVO webhookVO);

    void watchRemove(WebhookVO webhookVO);
}
