package com.example.batch.service.auth.database.rep.jpa.wallet;


import com.example.batch.common.database.rep.jpa.CommonDateEntity;
import com.example.batch.service.auth.database.rep.jpa.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PUB_WALLET_TB")
public class WalletEntity extends CommonDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "point", nullable = false)
    private Long point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public void buy(Double price) {
        this.point = Math.round(this.point - price);
    }
    public void sell(Double price) {
        this.point = Math.round(this.point + price);
    }
}