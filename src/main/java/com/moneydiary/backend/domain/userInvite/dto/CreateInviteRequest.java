package com.moneydiary.backend.domain.userInvite.dto;

import lombok.Data;

@Data
public class CreateInviteRequest {

    private Long userId;
    private Long groupId;
    private Long inviterId;

    public CreateInviteRequest(Long userId, Long groupId, Long inviterId) {
        this.userId = userId;
        this.groupId = groupId;
        this.inviterId = inviterId;
    }
}
