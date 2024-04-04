package com.example.batch.service.coin.api.biz.ins;

import com.example.batch.service.coin.api.vo.BitHumbDataVO;
import com.example.batch.service.coin.api.vo.BitHumbResultVO;
import com.example.batch.service.coin.database.rep.jpa.coin.CoinEntity;
import com.example.batch.service.coin.database.rep.jpa.coin.CoinREP;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Service
public class InsCoinServiceImpl implements InsCoinService {
    private final RestTemplate restTemplate;
    private final CoinREP coinREP;

    @Transactional
    @Override
    public void saveCoinDataBTC() {
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.bithumb.com")
                .path("/public/ticker/ALL")
                .encode()
                .build()
                .toUri();

        BitHumbResultVO forObject = restTemplate.getForObject(uri, BitHumbResultVO.class);

        ObjectMapper objectMapper = new ObjectMapper();
        BitHumbDataVO bitHumbDataVO = objectMapper.convertValue(forObject.getData().get("BTC"), BitHumbDataVO.class);

        CoinEntity coinEntity = CoinEntity.builder()
                .openingPrice(bitHumbDataVO.getOpeningPrice())
                .closingPrice(bitHumbDataVO.getClosingPrice())
                .minPrice(bitHumbDataVO.getMinPrice())
                .maxPrice(bitHumbDataVO.getMaxPrice())
                .unitsTraded(bitHumbDataVO.getUnitsTraded())
                .accTradeValue(bitHumbDataVO.getAccTradeValue())
                .prevClosingPrice(bitHumbDataVO.getPrevClosingPrice())
                .unitsTraded24H(bitHumbDataVO.getUnitsTraded24H())
                .accTradeValue24H(bitHumbDataVO.getAccTradeValue24H())
                .fluctate24H(bitHumbDataVO.getFluctate24H())
                .fluctateRate24H(bitHumbDataVO.getFluctateRate24H())
                .build();

        coinREP.save(coinEntity);
    }
}
