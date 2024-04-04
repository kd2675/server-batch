package com.example.batch.utils;

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
    private final RestTemplate restTemplate;
    @Override
    public ResponseEntity<MattermostPostVO> send(String message, String channelId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("urhoyjtmgjytmepm399nb476mr");

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
        return send(message, "947q6tnbc3gw9k9uwyxtboqx5h");
    }

    @Override
    public ResponseEntity<MattermostPostVO> sendNewsChannel(String message) {
        return send(message, "sph9p8g1uiygindx7qh8tnxmgr");
    }

    @Override
    public ResponseEntity<MattermostChannelVO> selectAllChannel(String channelId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("urhoyjtmgjytmepm399nb476mr");

        // Request Body 설정
        JSONObject requestBody = new JSONObject();

        // Request Entity 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // API 호출
        String url = "http://210.123.252.85:8066/api/v4/channels/"+channelId+"/posts?page=0&per_page=1000";

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
        headers.setBearerAuth("urhoyjtmgjytmepm399nb476mr");

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
