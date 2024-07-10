package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsEntity;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsREP;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
@Service
public class WebhookSVCImpl implements WebhookSVC, WebhookCMD {
    private final MattermostUtil mattermostUtil;

    private final OldNewsREP oldNewsREP;

    @Override
    public void cmdCall(WebhookVO webhookVO) {
        String text = webhookVO.getText();
        String[] split = text.split(" ");
        String cmd = split[0];

        if (cmd.equals(WebhookEnum.COMMAND_0.getKey())) {
            this.time();
        } else if (cmd.equals(WebhookEnum.COMMAND_1.getKey())) {
            this.uptime();
        }  else if (cmd.equals(WebhookEnum.COMMAND_2.getKey())) {
            this.news(webhookVO);
        } else {
            this.help();
        }
    }

    @Override
    public void news(WebhookVO webhookVO){
        String[] split = webhookVO.getText().split(" ");
        if (split.length != 4) {
            this.help();
            return;
        }

        String searchText = split[1].replace(",", "|");
        int pageNo = Integer.parseInt(split[2]);
        int pagePerCnt = Integer.parseInt(split[3]);
        if (pagePerCnt > 10) {
            this.help();
            return;
        }

        Pageable pageable = PageRequest.of(pageNo, pagePerCnt);
        List<OldNewsEntity> search = oldNewsREP.search(searchText, pageable);

        mattermostUtil.sendBobChannel(convertNewsMattermostMessage(search));
    }

    private String convertNewsMattermostMessage(List<OldNewsEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| 시각 | 제목 |\n";
        String line = "| :-:|:--: |\n";
//        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
//        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<OldNewsEntity> q = new LinkedList<>(entityList);
        while (!q.isEmpty()) {
            String content = "";
            for (int i = 0; i < 1; i++) {
                if (q.isEmpty()) {
                    break;
                }
                OldNewsEntity remove = q.remove();

                content += "| " + dtf.format(remove.getPubDate())
                        + " | " + "[" + remove.getTitle().replaceAll(regexEmojis, "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("♥", "")
                        .replace("|", "") + "]" + "(" + remove.getLink() + ")";
            }
            content += " |\n";
            result.append(content);
        }

        return result.toString();
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
    public void time() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target =
                LocalDateTime.of(
                        now.getYear(),
                        now.getMonth(),
                        now.getDayOfMonth(),
                        18,
                        0,
                        0
                );

        if (now.isBefore(target)) {
            Duration between = Duration.between(now, target);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            LocalDateTime localDateTime =
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            Long.valueOf(hour).intValue(),
                            Long.valueOf(minute).intValue(),
                            Long.valueOf(seconds).intValue()
                    );

            String fullTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            String partTime =
                    localDateTime.minusHours(1)
                            .minusMinutes(30)
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            StringBuilder str = new StringBuilder();
            str.append(fullTime)
                    .append(" 남았습니다.")
                    .append("\n")
                    .append(partTime)
                    .append("(점심시간 제외) 남았습니다.");

            mattermostUtil.sendBobChannel(str.toString());
        } else {
            mattermostUtil.sendBobChannel("퇴근하세요");
        }
    }

    @Override
    public void uptime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target =
                LocalDateTime.of(
                        now.getYear(),
                        now.getMonth(),
                        now.getDayOfMonth(),
                        8,
                        30,
                        0
                );

        if (now.isAfter(target)) {
            Duration between = Duration.between(target, now);
            long seconds = between.getSeconds();
            long minute = seconds / 60;
            seconds = seconds % 60;
            long hour = minute / 60;
            minute = minute % 60;

            String format =
                    LocalDateTime.of(
                            now.getYear(),
                            now.getMonth(),
                            now.getDayOfMonth(),
                            Long.valueOf(hour).intValue(),
                            Long.valueOf(minute).intValue(),
                            Long.valueOf(seconds).intValue()
                    ).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            mattermostUtil.sendBobChannel(format + " 지났습니다.");
        } else {
            mattermostUtil.sendBobChannel("출근하세요");
        }
    }
}
