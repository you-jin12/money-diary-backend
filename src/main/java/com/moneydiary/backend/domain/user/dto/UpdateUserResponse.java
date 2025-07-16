package com.moneydiary.backend.domain.user.dto;

import lombok.Data;

@Data
public class UpdateUserResponse {

    private String userId;
    private String userName;
    private String nickName;
    private boolean joinPolicy;
    private String profileImg;


    public UpdateUserResponse(String userId, String userName, String nickName, boolean joinPolicy,String profileImg) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.joinPolicy=joinPolicy;
        this.profileImg=profileImg;
    }
}
