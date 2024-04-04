package com.example.batch.service.news.batch.biz.ins;

import com.example.batch.service.news.api.vo.NaverNewsApiItemVO;
import com.example.batch.service.news.api.vo.NaverNewsApiVO;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.utils.NaverApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class InsNewsSVCImpl implements InsNewsSVC {
    private final NaverApiUtil naverApiUtil;

    private static final DateTimeFormatter RFC_1123_DATE_TIME = DateTimeFormatter.RFC_1123_DATE_TIME;

    @Override
    @Transactional
    public void saveNews() {
//        readNews().forEach(v -> writeNews(processNews(v)));
    }

    @Override
    public ItemReader<NaverNewsApiItemVO> readNews() {

        LocalDateTime LOCAL_DATE_TIME_1 = LocalDateTime.now().minusMinutes(5);
        LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.now().minusMinutes(11);


        Set<NaverNewsApiItemVO> set = new HashSet<>();

        String[] strings = {"속보", "ai", "주식", "축구", "코인", "날씨", "이", "가", "다", "는", "을", "고", "하", "에",};

        //이 가 다 는 을 고 하 에
        for (String s : strings) {
            int start = 1;

            do {
                List<NaverNewsApiItemVO> items = getItems(s, start);
                set.addAll(items.stream()
                        .peek(v -> v.setCategory(s))
                        .filter(v -> LocalDateTime.parse(v.getPubDate(), RFC_1123_DATE_TIME).isAfter(LOCAL_DATE_TIME_2)
                                && LocalDateTime.parse(v.getPubDate(), RFC_1123_DATE_TIME).isBefore(LOCAL_DATE_TIME_1))
                        .toList()
                );
                start += 100;

                if (items.stream()
                        .anyMatch(v -> LocalDateTime.parse(v.getPubDate(), RFC_1123_DATE_TIME).isBefore(LOCAL_DATE_TIME_2))
                ) {
                    start = 1100;
                }
            } while (start < 1000);
        }

        List<NaverNewsApiItemVO> news = new ArrayList<>(set);
        Collections.sort(news);

        return new ListItemReader<>(news);
    }

    @Override
    public ItemProcessor<NaverNewsApiItemVO, NewsEntity> processNews() {
        ItemProcessor<NaverNewsApiItemVO, NewsEntity> itemProcessor = new ItemProcessor<>() {
            @Override
            public NewsEntity process(NaverNewsApiItemVO item) throws Exception {
                NewsEntity newsEntity = NewsEntity.builder()
                        .category(item.getCategory())
                        .company("naverApi")
                        .title(item.getTitle())
                        .content(item.getDescription())
                        .link(item.getLink())
                        .pubDate(LocalDateTime.parse(item.getPubDate(), RFC_1123_DATE_TIME))
                        .build();

                return newsEntity;
            }
        };

        return itemProcessor;
    }

    @Override
    public ItemWriter<NewsEntity> writeNews(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<NewsEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private List<NaverNewsApiItemVO> getItems(String query, int start) {
        try {
            ResponseEntity conn = naverApiUtil.conn(query, 100, start, "date");
            String body = (String) conn.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            NaverNewsApiVO naverNewsApiVO = objectMapper.readValue(body, NaverNewsApiVO.class);

            return naverNewsApiVO.getItems();
        } catch (JsonProcessingException e) {
            log.error("{}", e);
        }
        return new ArrayList<>();
    }
}
