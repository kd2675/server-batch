package com.example.batch.utils;

import com.example.batch.service.news.api.vo.NaverNewsApiItemVO;
import com.example.batch.service.news.api.vo.NaverNewsApiVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ActiveProfiles("test")
@SpringBootTest(classes = {RestTemplate.class})
class NaverApiUtilImplTest {
    private static final String NAVER_API_URL = "https://openapi.naver.com";
    private static final String NAVER_API_PATH = "/v1/search/news.json";
    private static final String NAVER_API_CLIENT_ID = "97avHwhY7N2bJ4RysxAx";
    private static final String NAVER_API_CLIENT_SECRET = "74r7XpIXPi";

    @Autowired
    RestTemplate restTemplate;
    @Test
    void conn() throws JsonProcessingException {
        //이 가 다 는 을 고 하 에
        //속보 코인 주식(주가)
        String query = "속보";
        int display = 100;
        int start = 1;
        String sort = "date";

        URI uri = UriComponentsBuilder
                .fromUriString(NAVER_API_URL)
                .path(NAVER_API_PATH)
                .queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", NAVER_API_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_API_CLIENT_SECRET);

        // Request Body 설정
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("message", message);
//        requestBody.put("username", "systemBot");
//        requestBody.put("channel_id", channelId);

        // Request Entity 생성
//        HttpEntity entity = new HttpEntity(requestBody.toString(), headers);
        HttpEntity entity = new HttpEntity(headers);

        // API 호출
        ResponseEntity response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        String body = (String)response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        NaverNewsApiVO naverNewsApiVO = objectMapper.readValue(body, NaverNewsApiVO.class);

        for (NaverNewsApiItemVO vo : naverNewsApiVO.getItems()) {
            System.out.println(vo.toString());
            LocalDateTime localDateTime = LocalDateTime.parse(vo.getPubDate(), DateTimeFormatter.RFC_1123_DATE_TIME);
            System.out.println("time : " + localDateTime.toString());
        }
    }
}