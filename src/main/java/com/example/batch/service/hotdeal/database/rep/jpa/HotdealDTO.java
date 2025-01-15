package com.example.batch.service.hotdeal.database.rep.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.example.database.common.rep.jpa.CommonDateEntity;
import org.hibernate.annotations.DynamicInsert;

@Data
@AllArgsConstructor
public class HotdealDTO extends CommonDateEntity {
    private Long id;
    private Long productId;
    private String title;
    private int price;
    private String priceSlct;
    private String priceStr;
    private String link;
    private String img;
    private String shop;
    private String site;
    private String sendYn;

    public String getImgUrl100X100(){
        return "![](" + this.img + "?d=100x100" + ")";
    }
}
