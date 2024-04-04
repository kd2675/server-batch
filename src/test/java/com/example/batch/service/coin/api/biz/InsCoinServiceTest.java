package com.example.batch.service.coin.api.biz;

import com.example.batch.service.coin.api.vo.BitHumbDataVO;
import com.example.batch.service.coin.api.vo.BitHumbResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {RestTemplate.class})
class InsCoinServiceTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    void getCoinDataAll() {
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.bithumb.com")
                .path("/public/ticker/ALL")
                .encode()
                .build()
                .toUri();

        BitHumbResultVO forObject = restTemplate.getForObject(uri, BitHumbResultVO.class);

        ObjectMapper objectMapper = new ObjectMapper();
        BitHumbDataVO bitHumbDataVO = objectMapper.convertValue(forObject.getData().get("BTC"), BitHumbDataVO.class);

        System.out.println(bitHumbDataVO.toString());
    }
}