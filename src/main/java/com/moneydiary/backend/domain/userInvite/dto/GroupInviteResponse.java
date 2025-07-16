package com.moneydiary.backend.domain.userInvite.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupInviteResponse {

    private Long inviteId;
    private Long userId;
    private String nickName;
    private String profileImg;
    private LocalDateTime inviteDate;

    public GroupInviteResponse(Long inviteId, Long userId, String nickName, String profileImg, LocalDateTime inviteDate) {
        this.inviteId = inviteId;
        this.userId = userId;
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.inviteDate = inviteDate;
    }
}
