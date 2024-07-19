package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.music.database.rep.jpa.movie.WatchEntity;
import com.example.batch.service.music.database.rep.jpa.movie.WatchREP;
import com.example.batch.service.music.database.rep.jpa.music.PlaylistEntity;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WatchSVCImpl implements WatchSVC {
    private final MattermostUtil mattermostUtil;

    private final WatchREP watchREP;

    @Override
    public void notRun() {
        mattermostUtil.sendBobChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요");
    }

    @Override
    public void watch() {
        watchREP.findWatchRand().ifPresentOrElse(
                (musicEntity) -> {
                    String title = musicEntity.getTitle();

                    String str = title;
                    mattermostUtil.sendBobChannel(str);
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
            mattermostUtil.sendBobChannel(this.convertMattermostStr(content));
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

            WatchEntity watchEntity = WatchEntity.builder()
                    .title(title)
                    .star(starInfo)
                    .build();

            watchREP.save(watchEntity);

            mattermostUtil.sendBobChannel("완료");
        } catch (NumberFormatException e) {
            mattermostUtil.sendBobChannel("에러");
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

            mattermostUtil.sendBobChannel("완료");
        } catch (Exception e) {
            mattermostUtil.sendBobChannel("에러");
            log.error("watchRemove error", e);
        }
    }
}
