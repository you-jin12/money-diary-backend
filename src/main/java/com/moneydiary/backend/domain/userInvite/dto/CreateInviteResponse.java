package com.moneydiary.backend.domain.userInvite.dto;

import lombok.Data;

@Data
public class CreateInviteResponse {
    //user pk
    private Long id;
    private String nickName;
    private String profileImg;

    public CreateInviteResponse(Long id, String nickName, String profileImg) {
        this.id = id;
        this.nickName = nickName;
        this.profileImg = profileImg;
    }
}
