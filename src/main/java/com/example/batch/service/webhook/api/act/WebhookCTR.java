package com.example.batch.service.webhook.api.act;

import com.example.batch.service.webhook.api.biz.WebhookCMD;
import com.example.batch.service.webhook.api.biz.WebhookSVC;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WebhookCTR {
    private final WebhookSVC webhookSVC;
    private final WebhookCMD webhookCMD;
    private final MattermostUtil mattermostUtil;

    @PostMapping("test")
    public String test(@RequestBody final WebhookVO webhookVO){
        String text = webhookVO.getText().replace(webhookVO.getTriggerWord() + " ", "");

        mattermostUtil.sendBotChannel(text);

        return "OK";
    }

    @PostMapping("/")
    public HashMap<String, String> webhook(@RequestBody final WebhookVO webhookVO){
        webhookVO.setWebhookType("a");

        webhookCMD.cmdCall(webhookVO);

        return new HashMap<>();
    }

    @PostMapping("/cmd")
    public HashMap<String, String> command(@RequestBody final WebhookVO webhookVO){
        webhookVO.setWebhookType("b");

        webhookCMD.cmdCall(webhookVO);

        return new HashMap<>();
    }
}
