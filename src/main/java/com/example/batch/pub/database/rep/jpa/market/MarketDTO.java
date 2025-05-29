package com.example.batch.pub.database.rep.jpa.market;

import lombok.Getter;
import lombok.ToString;
import org.example.database.auth.database.rep.jpa.user.UserDTO;
import org.example.database.common.rep.jpa.CommonDateDTO;
import org.example.database.common.rep.jpa.CommonDateEntity;

@ToString
@Getter
public class MarketDTO extends CommonDateDTO {
    private Long id;
    //BTC
    private String coinSlct;
    //l, s
    private String marginSlct;
    private Integer margin;
    private Double price;
    private Double cnt;
    private Double cleanPrice;
    private UserDTO userDTO;

    public static MarketDTO of(MarketEntity marketEntity) {
        return new MarketDTO(
                marketEntity,
                marketEntity.getId(),
                marketEntity.getCoinSlct(),
                marketEntity.getMarginSlct(),
                marketEntity.getMargin(),
                marketEntity.getPrice(),
                marketEntity.getCnt(),
                marketEntity.getCleanPrice(),
                UserDTO.of(marketEntity.getUserEntity())
        );
    }

    private MarketDTO() {
    }

    private <T extends CommonDateEntity> MarketDTO(T t, Long id, String coinSlct, String marginSlct, Integer margin, Double price, Double cnt, Double cleanPrice, UserDTO userDTO) {
        super(t);
        this.id = id;
        this.coinSlct = coinSlct;
        this.marginSlct = marginSlct;
        this.margin = margin;
        this.price = price;
        this.cnt = cnt;
        this.cleanPrice = cleanPrice;
        this.userDTO = userDTO;
    }
}
