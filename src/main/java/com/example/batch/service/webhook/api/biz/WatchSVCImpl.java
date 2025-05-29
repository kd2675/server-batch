package com.example.batch.service.webhook.api.biz;

import com.example.batch.common.database.rep.jpa.watch.WatchEntity;
import com.example.batch.common.database.rep.jpa.watch.WatchREP;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WatchSVCImpl implements WatchSVC {
    private final MattermostUtil mattermostUtil;

    private final WatchREP watchREP;

    @Override
    public void notRun(WebhookVO webhookVO) {
        mattermostUtil.sendWebhookChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요", webhookVO);
    }

    @Override
    public void watch(WebhookVO webhookVO) {
        watchREP.findWatchRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();

                    String str = title;
                    mattermostUtil.sendWebhookChannel(str, webhookVO);
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

        List<WatchEntity> content = watchREP.findByWatchYnOrderByIdDesc("n", pageable);

        if (!content.isEmpty()) {
            mattermostUtil.sendWebhookChannel(this.convertMattermostStr(content), webhookVO);
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
                this.notRun(webhookVO);
                return;
            }

            String title = split[1];
            int starInfo = Integer.parseInt(split[2].trim());

            if (0 > starInfo || starInfo > 100) {
                mattermostUtil.sendWebhookChannel("평점은 0부터 100까지 입니다.", webhookVO);
                return;
            }

            WatchEntity watchEntity = WatchEntity.builder()
                    .title(title)
                    .star(starInfo)
                    .build();

            watchREP.save(watchEntity);

            mattermostUtil.sendWebhookChannel("완료", webhookVO);
        } catch (NumberFormatException e) {
            mattermostUtil.sendWebhookChannel("에러", webhookVO);
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
    public void watchY(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            Optional<WatchEntity> watchEntity = watchREP.findById(id);
            watchEntity.ifPresent(
                    (v->{
                        v.updateWatchYn("y");
                        watchREP.save(v);
                    })
            );

            mattermostUtil.sendWebhookChannel("완료", webhookVO);
        } catch (Exception e) {
            mattermostUtil.sendWebhookChannel("에러", webhookVO);
            log.error("watchRemove error", e);
        }
    }

    @Override
    public void watchRemove(WebhookVO webhookVO) {
        try {
            Long id = Long.valueOf(webhookVO.getText().split(" ")[1]);

            watchREP.deleteById(id);

            mattermostUtil.sendWebhookChannel("완료", webhookVO);
        } catch (Exception e) {
            mattermostUtil.sendWebhookChannel("에러", webhookVO);
            log.error("watchRemove error", e);
        }
    }
}
