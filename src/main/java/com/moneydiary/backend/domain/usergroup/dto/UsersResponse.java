package com.moneydiary.backend.domain.usergroup.dto;

import lombok.Data;

@Data
public class UsersResponse {

    private Long id;
    private String nickName;
    private String profileImg;

    public UsersResponse(Long id, String nickName, String profileImg) {
        this.id = id;
        this.nickName = nickName;
        this.profileImg = profileImg;
    }
}
