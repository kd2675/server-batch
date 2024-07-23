package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;

public interface MusicSVC extends NotRunSVC{
    void insMusic();

    void music(WebhookVO webhookVO);

    void musicSearch(WebhookVO webhookVO);

    void musicPlay(WebhookVO webhookVO);

    void playlist(WebhookVO webhookVO);

    void playlistAdd(WebhookVO webhookVO);

    void playlistRemove(WebhookVO webhookVO);
}
