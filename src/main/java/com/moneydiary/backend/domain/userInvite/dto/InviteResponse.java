package com.moneydiary.backend.domain.userInvite.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InviteResponse {

    private Long id;
    private Long groupId;
    private String groupName;
    private Long inviterId;
    private String inviterNickName;
    private LocalDateTime inviteDate;

    public InviteResponse(Long id, Long groupId, String groupName, Long inviterId, String inviterNickName, LocalDateTime inviteDate) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.inviterId = inviterId;
        this.inviterNickName = inviterNickName;
        this.inviteDate = inviteDate;
    }
}
