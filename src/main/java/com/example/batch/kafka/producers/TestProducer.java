package com.example.batch.kafka.producers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.vo.KafkaPayloadVO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(){
        KafkaPayloadVO message = new KafkaPayloadVO("test");

        log.info("send message: {}", message);
        kafkaTemplate.send("test", message);
    }
}
