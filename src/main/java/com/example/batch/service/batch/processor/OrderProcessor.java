package com.example.batch.service.batch.processor;

import com.example.batch.service.batch.common.BasicProcessor;
import com.example.batch.service.cocoin.database.rep.jpa.order.OrderEntity;
import com.example.batch.service.coin.database.rep.jpa.coin.CoinEntity;
import com.example.batch.service.coin.database.rep.jpa.coin.CoinREP;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrderProcessor {
    private static final String ORDER_PROCESSOR = "orderProcessor";

    private final CoinREP coinREP;

    @Bean(name = ORDER_PROCESSOR)
    @StepScope
    public BasicProcessor<OrderEntity, OrderEntity> itemProcessor() {

        CoinEntity coinEntity = coinREP.findTop1ByOrderByIdDesc().stream().findFirst().orElseGet(CoinEntity::new);
        int closingPrice = Integer.parseInt(coinEntity.getClosingPrice());

        return new BasicProcessor<OrderEntity, OrderEntity>() {
            @Override
            public OrderEntity process(OrderEntity orderEntity) throws Exception {

                orderEntity.getPrice();

                if (orderEntity.getPrice() < closingPrice) {
                    return null;
                }

                return orderEntity;
            }
        };
//        return CoinEntity::getId;
//        return item -> CoinDTO.ofEntity(item).getId();
//        return item -> item;
    }
}
