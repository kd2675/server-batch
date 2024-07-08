package com.example.batch.service.webhook.api.act;

import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WebhookCTR {
    private final MattermostUtil mattermostUtil;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/time")
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

            mattermostUtil.sendBobChannel(hour+":"+minute+":"+seconds+" 남았습니다.");
        }else{
            mattermostUtil.sendBobChannel("퇴근하세요");
        }

        return "ok";
    }
}
