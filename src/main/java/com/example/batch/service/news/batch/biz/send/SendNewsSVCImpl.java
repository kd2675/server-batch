package com.example.batch.service.news.batch.biz.send;

import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentEntity;
import com.example.batch.service.mattermost.database.rep.jpa.mattermost.sent.MattermostSentREP;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsREP;
import com.example.batch.utils.Levenshtein;
import com.example.batch.utils.MattermostUtil;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
@Primary
public class SendNewsSVCImpl implements SendNewsSVC {
    private final NewsREP newsREP;
    private final MattermostSentREP mattermostSentREP;

    private final MattermostUtil mattermostUtil;
    private final Levenshtein levenshtein;

    @Override
    public ListItemReader<NewsEntity> readNews(EntityManagerFactory entityManagerFactory) {
//        List<NewsEntity> newsList = newsREP.findTop10By();
        List<NewsEntity> newsList = newsREP.findTop15BySendYnOrderByIdDesc("n");

        return new ListItemReader<>(newsList);

//        Set<NewsEntity> setList = new HashSet<>(newsList);
//
//        Queue<NewsEntity> queueList = new LinkedList<>();
//
//        for (NewsEntity set : setList) {
//            boolean similar = false;
//
//            for (NewsEntity queue : queueList) {
//                double similarityTitle = levenshtein.similarity(set.getTitle(), queue.getTitle());
//                double similarityContent = levenshtein.similarity(set.getContent(), queue.getContent());
//                if (similarityTitle > 0.3D || similarityContent > 0.3D) {
//                    similar = true;
//                    break;
//                }
//            }
//
//            if (!similar) {
//                queueList.add(set);
//            }
//        }
//
//        return new ListItemReader<>(queueList.stream().toList());
    }

    @Override
    public ItemProcessor<NewsEntity, NewsEntity> processNews() {

        return item -> {
            item.updSendYn("y");
            return item;
        };
    }

    @Override
    public ItemWriter<NewsEntity> sendNews(EntityManagerFactory entityManagerFactory) {
        return chunk -> mattermostSentREP
                .save(MattermostSentEntity
                        .builder()
                        .sentId(mattermostUtil
                                .sendNewsChannel(convertMattermostMessage(chunk))
                                .getBody()
                                .getId())
                        .build());
    }

    @Override
    public ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory) {

        return new JpaItemWriterBuilder<NewsEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    public String convertMattermostMessage(Chunk<? extends NewsEntity> entityList) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regexEmojis = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";

        String header = "| 시각 | 제목 | 시각 | 제목 |\n";
        String line = "| :-:|:--:|:-:|:--: |\n";
        result.append(header)
                .append(line);


        Queue<NewsEntity> q = new LinkedList<>(entityList.getItems());
        while (!q.isEmpty()) {
            String content = "";
            for (int i = 0; i < 2; i++) {
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
}
