package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsREP;
import com.example.batch.service.news.database.rep.jpa.news.NewsSpec;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsEntity;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsREP;
import com.example.batch.service.news.database.rep.jpa.oldnews.OldNewsSpec;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.service.webhook.api.vo.WebhookEnum;
import com.example.batch.utils.BugsApiUtil;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.YoutubeApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookSVCImpl implements WebhookCMD, WebhookSVC {
    private final MattermostUtil mattermostUtil;
    private final BugsApiUtil bugsApiUtil;
    private final YoutubeApiUtil youtubeApiUtil;

    private final MusicSVC musicSVC;
    private final WatchSVC watchSVC;

    private final NewsREP newsREP;
    private final OldNewsREP oldNewsREP;

    @Override
    public void notRun(){
        mattermostUtil.sendBotChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요");
    }

    @Transactional
    @Override
    public void cmdCall(WebhookVO webhookVO) {
        String cmd = webhookVO.getText().split(" ")[0];

        Map<String, Runnable> commandMap = new HashMap<>();
        commandMap.put(WebhookEnum.COMMAND.getKey(), this::help);
        commandMap.put(WebhookEnum.COMMAND_100.getKey(), this::time);
        commandMap.put(WebhookEnum.COMMAND_101.getKey(), this::uptime);
        commandMap.put(WebhookEnum.COMMAND_200.getKey(), () -> this.news(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_201.getKey(), () -> this.oldNews(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_300.getKey(), musicSVC::music);
        commandMap.put(WebhookEnum.COMMAND_301.getKey(), () -> musicSVC.musicSearch(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_302.getKey(), () -> musicSVC.musicPlay(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_303.getKey(), () -> musicSVC.playlist(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_304.getKey(), () -> musicSVC.playlistAdd(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_305.getKey(), () -> musicSVC.playlistRemove(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_400.getKey(), watchSVC::watch);
        commandMap.put(WebhookEnum.COMMAND_401.getKey(), () -> watchSVC.watchList(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_402.getKey(), () -> watchSVC.watchAdd(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_403.getKey(), () -> watchSVC.watchY(webhookVO));
        commandMap.put(WebhookEnum.COMMAND_404.getKey(), () -> watchSVC.watchRemove(webhookVO));

        for (WebhookEnum webhookEnum : WebhookEnum.values()) {
            commandMap.put(webhookEnum.getShortKey(), commandMap.get(webhookEnum.getKey()));
        }

        commandMap.getOrDefault(cmd, this::notRun).run();
    }

    @Transactional(readOnly = true)
    @Override
    public void news(WebhookVO webhookVO) {
        String[] args = webhookVO.getText().split(" ");
        if (!isValidInput(args)) {
            this.notRun();
            return;
        }

        String searchText = args[1];
        int pageNo = Integer.parseInt(args[2]);
        int pagePerCnt = Integer.parseInt(args[3]);

        List<NewsEntity> newsEntities = searchNews(searchText, pageNo, pagePerCnt);
        if (!newsEntities.isEmpty()) {
            mattermostUtil.sendBotChannel(convertNewsMattermostMessage(newsEntities));
        }
    }

    private boolean isValidInput(String[] args) {
        return args.length == 4 && Integer.parseInt(args[3]) <= 10;
    }

    private List<NewsEntity> searchNews(String searchText, int pageNo, int pagePerCnt) {
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        List<String> searchTerms = Arrays.asList(searchText.split(","));
        return newsREP.findAll(NewsSpec.searchWith(searchTerms), pageable).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public void oldNews(WebhookVO webhookVO) {
        String[] args = webhookVO.getText().split(" ");
        if (!isValidInput(args)) {
            this.notRun();
            return;
        }

        String searchText = args[1];
        int pageNo = Integer.parseInt(args[2]);
        int pagePerCnt = Integer.parseInt(args[3]);

        List<OldNewsEntity> oldNewsEntities = searchOldNews(searchText, pageNo, pagePerCnt);
        if (!oldNewsEntities.isEmpty()) {
            mattermostUtil.sendBotChannel(convertOldNewsMattermostMessage(oldNewsEntities));
        }
    }

    private List<OldNewsEntity> searchOldNews(String searchText, int pageNo, int pagePerCnt) {
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        List<String> searchTerms = Arrays.asList(searchText.split(","));
        return oldNewsREP.findAll(OldNewsSpec.searchWith(searchTerms), pageable).getContent();
    }

    private String convertNewsMattermostMessage(List<NewsEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| 시각 | 제목 |\n";
        String line = "| :-:|:--: |\n";
//        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
//        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<NewsEntity> q = new LinkedList<>(entityList);
        while (!q.isEmpty()) {
            String content = "";
            for (int i = 0; i < 1; i++) {
                if (q.isEmpty()) {
                    break;
                }
                NewsEntity remove = q.remove();

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

    private String convertOldNewsMattermostMessage(List<OldNewsEntity> entityList) {
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
                    .append(", ")
                    .append(webhook.getShortKey())
                    .append(" : ")
                    .append(webhook.getValue())
                    .append("\n");
        }
        mattermostUtil.sendBotChannel(str.toString());
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

            String fullTime =
                    localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            String partTime =
                    localDateTime.minusHours(1)
                            .minusMinutes(30)
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            StringBuilder str = new StringBuilder();
            str.append(fullTime)
                    .append(" 남았습니다.");

            mattermostUtil.sendBotChannel(str.toString());
        } else {
            mattermostUtil.sendBotChannel("퇴근하세요");
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

            mattermostUtil.sendBotChannel(format + " 지났습니다.");
        } else {
            mattermostUtil.sendBotChannel("출근하세요");
        }
    }
}
