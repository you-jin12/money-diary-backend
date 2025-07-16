package com.moneydiary.backend.domain.usergroup.dto;

import lombok.Data;

@Data
public class JoinGroupRequest {

    private Long userId;
    private Long groupId;
    private String groupPassword;

}
