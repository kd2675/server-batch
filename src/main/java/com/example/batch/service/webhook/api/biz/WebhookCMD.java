package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;

public interface WebhookCMD {
    void cmdCall(final WebhookVO webhookVO);
}
