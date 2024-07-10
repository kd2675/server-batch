package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class WebhookSVCImpl implements WebhookSVC, WebhookCMD{
    private final MattermostUtil mattermostUtil;

    @Override
    public void cmdCall(WebhookVO webhookVO) {
        String text = webhookVO.getText();
        String[] split = text.split(" ");
        String cmd = split[0];

        if (cmd.equals(WebhookEnum.COMMAND_0.getKey())) {
            this.time();
        } else if (cmd.equals(WebhookEnum.COMMAND_1.getKey())) {
            this.uptime();
        } else {
            this.help();
        }
    }

    @Override
    public void help() {
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
    }

    @Override
    public void time(){
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
    }

    @Override
    public void uptime(){
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
    }
}
