package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.webhook.database.rep.jpa.movie.WatchEntity;
import com.example.batch.service.webhook.database.rep.jpa.movie.WatchREP;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WatchSVCImpl implements WatchSVC {
    private final MattermostUtil mattermostUtil;

    private final WatchREP watchREP;

    @Override
    public void notRun() {
        mattermostUtil.sendBotChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요");
    }

    @Override
    public void watch() {
        watchREP.findWatchRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();

                    String str = title;
                    mattermostUtil.sendBotChannel(str);
                },
                () -> {

                }
        );
    }

    @Override
    public void watchList(WebhookVO webhookVO) {
        Integer pageNo = 0;
        Integer pagePerCnt = 100;
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");

        Page<WatchEntity> all = watchREP.findAll(pageable);
        List<WatchEntity> content = all.getContent();

        if (!content.isEmpty()) {
            mattermostUtil.sendBotChannel(this.convertMattermostStr(content));
        }
    }

    private String convertMattermostStr(List<WatchEntity> entities) {
        StringBuilder sb = new StringBuilder();
//        String header = "| id | title | singer | pubDate | youtube |\n";
//        String line = "| :-:|:-:|:--:|:-:|:-: |\n";
//        sb.append(header)
//                .append(line);

        entities.forEach(v -> {
            sb.append("|");
            sb.append(v.getId());
            sb.append("|");
            sb.append(v.getTitle());
            sb.append("|");
            sb.append(v.getStar());
            sb.append("|");
            sb.append("\n");
        });

        return sb.toString();
    }

    @Override
    public void watchAdd(WebhookVO webhookVO) {
        try {
            String[] split = parseSplitText(webhookVO.getText());
            if (split.length < 2 || split.length > 3) {
                this.notRun();
                return;
            }

            String title = split[1];
            int starInfo = Integer.parseInt(split[2].trim());

            if (0 > starInfo || starInfo > 100) {
                mattermostUtil.sendBotChannel("평점은 0부터 100까지 입니다.");
                return;
            }

            WatchEntity watchEntity = WatchEntity.builder()
                    .title(title)
                    .star(starInfo)
                    .build();

            watchREP.save(watchEntity);

            mattermostUtil.sendBotChannel("완료");
        } catch (NumberFormatException e) {
            mattermostUtil.sendBotChannel("에러");
            log.error("watchAdd error", e);
        }
    }

    private String[] parseSplitText(String text) {
        if (text.contains("'") && text.length() == text.replace("'", "").length() + 2) {
            return text.split("'");
        } else if (text.contains("\"") && text.length() == text.replace("\"", "").length() + 2) {
            return text.split("\"");
        } else {
            if (text.split(" ")[1].startsWith("\'") && text.split(" ")[1].endsWith("\"")) {
                throw new RuntimeException("parseSplitText error");
            }
            return text.split(" ");
        }
    }

    @Override
    public void watchRemove(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            watchREP.deleteById(id);

            mattermostUtil.sendBotChannel("완료");
        } catch (Exception e) {
            mattermostUtil.sendBotChannel("에러");
            log.error("watchRemove error", e);
        }
    }
}
