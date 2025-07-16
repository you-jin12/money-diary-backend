package com.moneydiary.backend.domain.userInvite.dto;

import com.moneydiary.backend.domain.userInvite.InvitationStatus;
import com.moneydiary.backend.domain.usergroup.Role;
import lombok.Data;

@Data
public class UpdateInviteStatusRequest {

    private InvitationStatus invitationStatus;
    private Long userId;
}
