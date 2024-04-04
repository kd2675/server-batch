package com.example.batch.service.auth.database.rep.jpa.wallet;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class WalletDTO {
    private Long id;
    private Long point;
    private Long userId;

    public static WalletDTO of(WalletEntity walletEntity){
        return new WalletDTO(
                walletEntity.getId(),
                walletEntity.getPoint(),
                walletEntity.getUserEntity().getId()
        );
    }

    private WalletDTO() {
    }

    private WalletDTO(Long id, Long point, Long userId) {
        this.id = id;
        this.point = point;
        this.userId = userId;
    }
}
