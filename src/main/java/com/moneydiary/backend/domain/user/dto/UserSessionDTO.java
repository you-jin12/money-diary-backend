package com.moneydiary.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserSessionDTO {

    private Long id;
    private String userId;
    private String userName;
    private LocalDateTime loginTime;

    @Builder
    public UserSessionDTO(Long id,String userId, String userName, LocalDateTime loginTime) {
        this.id=id;
        this.userId = userId;
        this.userName = userName;
        this.loginTime = loginTime;
    }
}
