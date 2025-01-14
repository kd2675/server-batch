package com.example.batch.service.webhook.api.biz;

import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntity;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntityREP;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealSpec;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsSpec;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotdealSVCImpl implements HotdealSVC {
    private final MattermostUtil mattermostUtil;
    private final HotdealEntityREP hotdealEntityREP;

    @Override
    public void notRun(WebhookVO webhookVO) {
        mattermostUtil.sendWebhookChannel("잘못된 입력입니다. 설명을 보시려면 [$c]를 입력해주세요", webhookVO);
    }

    @Override
    @Transactional(readOnly = true)
    public void hotdealSearch(WebhookVO webhookVO) {
        String[] args = webhookVO.getText().split(" ");
        if (!isValidInput(args)) {
            this.notRun(webhookVO);
            return;
        }

        String searchText = args[1];
        int pageNo = args.length == 4 ? Integer.parseInt(args[2]) : 0;
        int pagePerCnt = args.length == 4 ? Integer.parseInt(args[3]) : 3;

        List<HotdealEntity> hotdealEntities = searchHotdeal(searchText, pageNo, pagePerCnt);
        if (!hotdealEntities.isEmpty()) {
            mattermostUtil.sendWebhookChannel(convertNewsMattermostMessage(hotdealEntities), webhookVO);
        } else {
            mattermostUtil.sendWebhookChannel("검색된 핫딜이 없습니다.", webhookVO);
        }
    }

    public List<HotdealEntity> searchHotdeal(String searchText, int pageNo, int pagePerCnt) {
        Pageable pageable = PageRequest.of(pageNo, pagePerCnt, Sort.Direction.DESC, "id");
        List<String> searchTerms = Arrays.asList(searchText.split(","));
        return hotdealEntityREP.findAll(HotdealSpec.searchWith(searchTerms), pageable).getContent();
    }

    private boolean isValidInput(String[] args) {
        return args.length == 4 && Integer.parseInt(args[3]) <= 10 || args.length == 2;
    }

    private String convertNewsMattermostMessage(List<HotdealEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| id | 시각 | img | 제목 | 가격 |\n";
        String line = "| :-:|:--:|:--:|:----:|:--: |\n";
//        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
//        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<HotdealEntity> q = new LinkedList<>(entityList);
        while (!q.isEmpty()) {
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < 1; i++) {
                if (q.isEmpty()) {
                    break;
                }
                HotdealEntity remove = q.remove();

                content.append("| ")
                        .append(remove.getId())
                        .append(" | ")

                        .append(dtf.format(remove.getCreateDate()))
                        .append(" | ")

                        .append(remove.getImgUrl100X100())
                        .append(" | ")

                        .append("[")
                        .append(remove.getTitle().replaceAll(regexEmojis, "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("♥", "")
                        .replace("|", ""))
                        .append("]")
                        .append("(")
                        .append(remove.getLink())
                        .append(")")
                        .append(" | ")

                        .append(remove.getPriceStr());
            }
            content.append(" |\n");
            result.append(content);
        }

        return result.toString();
    }

}
