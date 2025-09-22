package com.example.batch.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    
    // 최신 방식: KafkaProperties에서 직접 설정을 가져와서 사용
    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>();
        
        // Bootstrap servers 설정
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        
        // Producer 기본 설정들
        KafkaProperties.Producer producerProps = kafkaProperties.getProducer();
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProps.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProps.getValueSerializer());
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        config.put(ProducerConfig.RETRIES_CONFIG, producerProps.getRetries());

        // Properties에서 추가 설정들 가져오기
        if (producerProps.getProperties() != null) {
            config.putAll(producerProps.getProperties());
        }
        
        // 추가적인 Producer 설정 (타임아웃 및 재시도 최적화)
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);        // 30초
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60000);       // 60초
        config.put(ProducerConfig.LINGER_MS_CONFIG, 1);                     // 배치 지연 최소화
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);           // 재시도 간격
        
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
