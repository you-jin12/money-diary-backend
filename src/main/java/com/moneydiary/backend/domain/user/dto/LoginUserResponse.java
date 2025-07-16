package com.moneydiary.backend.domain.user.dto;

import lombok.Data;

@Data
public class LoginUserResponse {

    private Long id;
    private String userId;
    private String nickName;
    private String userName;

    private boolean joinPolicy;

    public LoginUserResponse(Long id,String userId, String nickName, String userName, boolean joinPolicy) {
        this.id=id;
        this.userId = userId;
        this.nickName = nickName;
        this.userName = userName;
        this.joinPolicy=joinPolicy;
    }
}
