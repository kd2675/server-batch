package com.example.batch.utils;

import com.example.batch.utils.enums.ChannelEnum;
import com.example.batch.utils.vo.MattermostChannelVO;
import com.example.batch.utils.vo.MattermostPostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class MattermostUtilImpl implements MattermostUtil {
    private static final String MATTERMOST_SYSTEM_BOT_TOKEN = "urhoyjtmgjytmepm399nb476mr";

    private final RestTemplate restTemplate;
    @Override
    public ResponseEntity<MattermostPostVO> send(String message, String channelId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(MATTERMOST_SYSTEM_BOT_TOKEN);

        // Request Body 설정
        JSONObject requestBody = new JSONObject();
        requestBody.put("message", message);
        requestBody.put("username", "system-bot");
        requestBody.put("channel_id", channelId);

        // Request Entity 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // API 호출
        String url = "http://210.123.252.85:8066/api/v4/posts";

        try {
            ResponseEntity<MattermostPostVO> response = restTemplate.exchange(url, HttpMethod.POST, entity, MattermostPostVO.class);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("mattermost send error -> {}", e.toString());
            throw e;
        }
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendCoinChannel(String message) {
        String channelId = ChannelEnum.MATTERMOST_CHANNEL_COIN.getValue();
        return send(message, channelId);
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendNewsChannel(String message) {
        String channelId = ChannelEnum.MATTERMOST_CHANNEL_NEWS.getValue();
        return send(message, channelId);
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendNewsFlashChannel(String message) {
        String channelId = ChannelEnum.MATTERMOST_CHANNEL_NEWS_FLASH.getValue();
        return send(message, channelId);
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendNewsMarketingChannel(String message) {
        String channelId = ChannelEnum.MATTERMOST_CHANNEL_NEWS_MARKETING.getValue();
        return send(message, channelId);
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendNewsStockChannel(String message) {
        String channelId = ChannelEnum.MATTERMOST_CHANNEL_NEWS_STOCK.getValue();
        return send(message, channelId);
    }

    @Override
    public ResponseEntity<MattermostChannelVO> selectAllChannel(String channelId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(MATTERMOST_SYSTEM_BOT_TOKEN);

        // Request Body 설정
        JSONObject requestBody = new JSONObject();

        // Request Entity 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // API 호출
        String url = "http://210.123.252.85:8066/api/v4/channels/"+channelId+"/posts?page=0&per_page=500";

        try {
            ResponseEntity<MattermostChannelVO> response = restTemplate.exchange(url, HttpMethod.GET, entity, MattermostChannelVO.class);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("mattermost selectAllChannel error -> {}", e.toString());
            throw e;
        }
    }

    @Override
    public ResponseEntity delete(String sentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(MATTERMOST_SYSTEM_BOT_TOKEN);

        // Request Body 설정
        JSONObject requestBody = new JSONObject();

        // Request Entity 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // API 호출
        String url = "http://210.123.252.85:8066/api/v4/posts/"+sentId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("mattermost delete error -> {}", e.toString());
            throw e;
        }
    }
}
