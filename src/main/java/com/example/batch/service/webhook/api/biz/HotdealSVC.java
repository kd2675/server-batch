package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface HotdealSVC extends NotRunSVC{
    void hotdealSearch(WebhookVO webhookVO);
    void hotdealSearchApi(WebhookVO webhookVO);
    void hotdealAlimIns(WebhookVO webhookVO);
    void hotdealAlimDel(WebhookVO webhookVO);
    void hotdealAlimList(WebhookVO webhookVO);
    void hotdealAlimBrandIns(WebhookVO webhookVO);
}
