package com.example.batch.service.webhook.api.act;

import com.example.batch.service.webhook.api.biz.WebhookCMD;
import com.example.batch.service.webhook.api.biz.WebhookSVC;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WebhookCTR {
    private final WebhookSVC webhookSVC;
    private final WebhookCMD webhookCMD;
    private final MattermostUtil mattermostUtil;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @PostMapping("test")
    public String test(@RequestBody final WebhookVO webhookVO){
        String text = webhookVO.getText().replace(webhookVO.getTriggerWord() + " ", "");

        mattermostUtil.sendBobChannel(text);

        return "OK";
    }

    @PostMapping("/help")
    public String command(){
        webhookSVC.help();

        return "OK";
    }

    @PostMapping("/cmd")
    public String command(@RequestBody final WebhookVO webhookVO){
        webhookCMD.cmdCall(webhookVO);

        return "OK";
    }
}
