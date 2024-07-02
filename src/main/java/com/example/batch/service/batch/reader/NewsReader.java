package com.example.batch.service.batch.reader;

import com.example.batch.service.batch.common.DelJpaPagingItemReader;
import com.example.batch.service.batch.enums.NewsKeywordEnum;
import com.example.batch.service.batch.step.NewsStep;
import com.example.batch.service.news.api.vo.NaverNewsApiItemVO;
import com.example.batch.service.news.api.vo.NaverNewsApiVO;
import com.example.batch.service.news.database.rep.jpa.news.NewsEntity;
import com.example.batch.service.news.database.rep.jpa.news.NewsREP;
import com.example.batch.utils.NaverApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class NewsReader {
    public static final String FIND_NAVER_NEWS_API = "findNaverNewsApi";
    public static final String FIND_TOP_15_BY_SEND_YN_ORDER_BY_ID_DESC = "findTop15BySendYnOrderByIdDesc";
    public static final String FIND_ALL_NEWS_FIX_PAGE_0 = "findAllNewsFixPage0";

    private final NewsREP newsREP;
    private final NaverApiUtil naverApiUtil;

    @Bean(name = FIND_NAVER_NEWS_API, destroyMethod = "")
    @StepScope
    public ListItemReader<NaverNewsApiItemVO> findNaverNewsApi() {
        return new ListItemReader<NaverNewsApiItemVO>(this.getNaverNewsApiItemVOS());
    }

    @Bean(name = FIND_TOP_15_BY_SEND_YN_ORDER_BY_ID_DESC, destroyMethod = "")
    @StepScope
    public ListItemReader<NewsEntity> findTop15BySendYnOrderByIdDesc(@Qualifier("newsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        List<String> newsKeywordValue = NewsKeywordEnum.getNewsKeywordValue();
        newsREP.findTop15BySendYnOrderByIdDescAndCategoryInOrderByIdDesc("n", newsKeywordValue);

        return new ListItemReader<>(
                newsREP.findTop15BySendYnOrderByIdDesc("n")
        );
    }

    @Bean(name = FIND_ALL_NEWS_FIX_PAGE_0, destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<NewsEntity> newsFindAllFixPage0(@Qualifier("newsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<NewsEntity> reader = new DelJpaPagingItemReader<>();

        reader.setName("jpaPagingItemReader");
        reader.setPageSize(NewsStep.PAGE_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT e FROM NewsEntity e WHERE e.pubDate < :date");

        HashMap<String, Object> param = new HashMap<>();
        param.put("date", LocalDateTime.now().minusMinutes(30));
        reader.setParameterValues(param);
        return reader;
    }

    private List<NaverNewsApiItemVO> getNaverNewsApiItemVOS() {
        LocalDateTime LOCAL_DATE_TIME_1 = LocalDateTime.now().minusMinutes(5);
        LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.now().minusMinutes(6);

        Set<NaverNewsApiItemVO> set = new HashSet<>();

        for (NewsKeywordEnum keyword : NewsKeywordEnum.values()) {
            String s = keyword.getValue();

            int start = 1;

            do {
                List<NaverNewsApiItemVO> items = getItems(s, start);
                set.addAll(items.stream()
                        .peek(v -> v.setCategory(s))
                        .filter(v -> LocalDateTime.parse(v.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).isAfter(LOCAL_DATE_TIME_2)
                                && LocalDateTime.parse(v.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).isBefore(LOCAL_DATE_TIME_1))
                        .toList()
                );
                start += 100;

                if (items.stream()
                        .anyMatch(v -> LocalDateTime.parse(v.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME).isBefore(LOCAL_DATE_TIME_2))
                ) {
                    start = 1100;
                }
            } while (start < 1000);
        }

        List<NaverNewsApiItemVO> news = new ArrayList<>(set);
        Collections.sort(news);

        return news;
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
