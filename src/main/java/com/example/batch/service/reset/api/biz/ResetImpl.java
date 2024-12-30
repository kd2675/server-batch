package com.example.batch.service.reset.api.biz;

import com.example.batch.service.batch.job.NewsJob;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentREP;
import com.example.batch.service.reset.database.rep.jpa.ResetPoint;
import com.example.batch.service.reset.database.rep.jpa.ResetPointREP;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.enums.ChannelEnum;
import com.example.batch.utils.vo.MattermostChannelVO;
import com.example.batch.utils.vo.MattermostPostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.batch.cron.Scheduler.getJobParameters;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResetImpl implements Reset {
    private final ResetPointREP resetPointREP;
    private final MattermostSentREP mattermostSentREP;

    private final MattermostUtil mattermostUtil;

    @Transactional
    @Override
    public void mattermostDelReset() throws Exception {
        List<ResetPoint> resetPoints = resetPointREP.findByResetYnAndPointIdInOrderByCreateDateDesc("n", Collections.singletonList(1));

        if (resetPoints.size() >= 3) {
            delChannelPost(ChannelEnum.MATTERMOST_CHANNEL_NEWS.getValue());
            delChannelPost(ChannelEnum.MATTERMOST_CHANNEL_NEWS_FLASH.getValue());
            delChannelPost(ChannelEnum.MATTERMOST_CHANNEL_NEWS_MARKETING.getValue());
            delChannelPost(ChannelEnum.MATTERMOST_CHANNEL_NEWS_STOCK.getValue());

            mattermostSentREP.deleteByCategory("news");

            for (ResetPoint resetPoint : resetPoints) {
                resetPoint.setResetY();
            }

            resetPointREP.saveAll(resetPoints);
        }
    }

    private void delChannelPost(String id) {
        for (;;) {
            ResponseEntity<MattermostChannelVO> channel = mattermostUtil.selectAllChannel(id);
            Map<String, MattermostPostVO> posts = channel.getBody().getPosts();

            System.out.println(channel.getBody().getNextPostId());
            System.out.println(posts.values().size());
            System.out.println(channel.getBody().getHasNext());

            int idx = 0;
            for (MattermostPostVO vo : posts.values()) {
                mattermostUtil.delete(vo.getId());
            }

            if (posts.values().size() < 100) {
                break;
            }
        }
    }
}
