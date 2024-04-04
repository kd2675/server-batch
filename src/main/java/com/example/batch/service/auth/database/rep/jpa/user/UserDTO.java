package com.example.batch.service.auth.database.rep.jpa.user;

import com.example.batch.common.database.rep.jpa.CommonDateDTO;
import com.example.batch.common.database.rep.jpa.CommonDateEntity;
import com.example.batch.service.auth.database.rep.jpa.auth.AuthDTO;
import com.example.batch.service.auth.database.rep.jpa.wallet.WalletDTO;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;
import java.util.stream.Collectors;

@ToString
@Getter
public class UserDTO extends CommonDateDTO {
    private Long id;
    private String oAuth2Id;
    private String email;
    private String name;
    private String phone;
    private String nick;
    private String ip;
    private String block;
    private WalletDTO walletDTO;
    private Set<AuthDTO> authDTOSet;

    public static UserDTO of(UserEntity userEntity) {
        return new UserDTO(
                userEntity,
                userEntity.getId(),
                userEntity.getOAuth2Id(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getPhone(),
                userEntity.getNick(),
                userEntity.getIp(),
                userEntity.getBlock(),
                WalletDTO.of(userEntity.getWalletEntity()),
                userEntity.getRole().stream()
                        .map(AuthDTO::of)
                        .collect(Collectors.toSet())
        );
    }

    private UserDTO() {
    }

    public <T extends CommonDateEntity> UserDTO(T t, Long id, String oAuth2Id, String email, String name, String phone, String nick, String ip, String block, WalletDTO walletDTO, Set<AuthDTO> authDTOSet) {
        super(t);
        this.id = id;
        this.oAuth2Id = oAuth2Id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.nick = nick;
        this.ip = ip;
        this.block = block;
        this.walletDTO = walletDTO;
        this.authDTOSet = authDTOSet;
    }
}
