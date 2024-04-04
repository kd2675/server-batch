package com.example.batch.service.auth.database.rep.jpa.auth;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthDTO {
    private Long id;
    private String role;
    private Long userId;

    public static AuthDTO of(AuthEntity authEntity){
        return new AuthDTO(
                authEntity.getId(),
                authEntity.getRole(),
                authEntity.getUserEntity().getId()
        );
    }

    private AuthDTO() {
    }

    private AuthDTO(Long id, String role, Long userId) {
        this.id = id;
        this.role = role;
        this.userId = userId;
    }
}
