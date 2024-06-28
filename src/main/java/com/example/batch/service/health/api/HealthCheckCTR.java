package com.example.batch.service.health.api;

import com.example.batch.service.batch.job.CoinJob;
import com.example.batch.service.batch.job.NewsJob;
import com.example.batch.service.news.batch.biz.ins.InsNewsSVC;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.vo.MattermostChannelVO;
import com.example.batch.utils.vo.MattermostPostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.batch.cron.Scheduler.getJobParameters;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class HealthCheckCTR {
    private final InsNewsSVC insNewsSVC;
    private final MattermostUtil mattermostUtil;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/health")
    public String health(){
        return "ok";
    }

    @GetMapping("/news/ins")
    public void test() {
        insNewsSVC.saveNews();
    }

    @PostMapping("/coin/send")
    public void coinSend() throws Exception {
        // add parameters as needed
        jobLauncher.run(jobRegistry.getJob(CoinJob.SEND_COIN_JOB), getJobParameters());
    }

    @PostMapping("/mattermost/del")
    public String test2() {
        del();

        return "ok";
    }

    @Async("asyncTaskExecutor")
    public void del() {
        ResponseEntity<MattermostChannelVO> channel = mattermostUtil.selectAllChannel("947q6tnbc3gw9k9uwyxtboqx5h");
        Map<String, MattermostPostVO> posts = channel.getBody().getPosts();

        System.out.println(channel.getBody().getHasNext());

        int idx = 0;
        for (MattermostPostVO vo : posts.values()) {
            mattermostUtil.delete(vo.getId());
        }
    }


    @DeleteMapping("/mattermost/job/news")
    public void delMattermostNewsJob() throws Exception {
        jobLauncher.run(jobRegistry.getJob(NewsJob.DEL_NEWS_JOB), getJobParameters());

    }
}
