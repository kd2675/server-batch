package com.example.batch.service.webhook.api.act;

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
    private final MattermostUtil mattermostUtil;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @PostMapping("test")
    public String test(@RequestBody final WebhookVO webhookVO){

        String text = webhookVO.getText().replace(webhookVO.getTriggerWord() + " ", "");

        mattermostUtil.sendBobChannel(text);
        return "OK";
    }

    @PostMapping("command")
    public String command(){
        StringBuilder str = new StringBuilder();

        for (WebhookEnum webhook : WebhookEnum.values()) {
            str.append(webhook.getId())
                    .append(". ")
                    .append(webhook.getKey())
                    .append(" : ")
                    .append(webhook.getValue())
                    .append("\n");
        }
        mattermostUtil.sendBobChannel(str.toString());

        return "OK";
    }

    @PostMapping("/time")
    public String time(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 18, 0, 0);

        if (now.isBefore(target)){
            Duration between = Duration.between(now, target);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            String format = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Long.valueOf(hour).intValue(), Long.valueOf(minute).intValue(), Long.valueOf(seconds).intValue()).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            mattermostUtil.sendBobChannel(format+" 남았습니다.");
        }else{
            mattermostUtil.sendBobChannel("퇴근하세요");
        }

        return "ok";
    }

    @PostMapping("/uptime")
    public String uptime(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 30, 0);

        if (now.isAfter(target)){
            Duration between = Duration.between(target, now);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            String format = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Long.valueOf(hour).intValue(), Long.valueOf(minute).intValue(), Long.valueOf(seconds).intValue()).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            mattermostUtil.sendBobChannel(format+" 지났습니다.");
        }else{
            mattermostUtil.sendBobChannel("출근하세요");
        }

        return "ok";
    }
}
