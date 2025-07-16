package com.moneydiary.backend.domain.group.dto;

import lombok.Data;

@Data
public class GroupResponse {

    private Long id;
    private String groupName;
    private Long hostId;
    private String groupHost;

    private int maxMember;
    private int currentMember;
    private String groupGoal;
    private String groupMemo;
    private String hostImg;

    private boolean isPrivate;

    public GroupResponse(Long id, String groupName, Long hostId, String groupHost, int maxMember, int currentMember, String groupGoal, String groupMemo, String hostImg, boolean isPrivate) {
        this.id = id;
        this.groupName = groupName;
        this.hostId = hostId;
        this.groupHost = groupHost;
        this.maxMember = maxMember;
        this.currentMember = currentMember;
        this.groupGoal = groupGoal;
        this.groupMemo = groupMemo;
        this.hostImg = hostImg;
        this.isPrivate = isPrivate;
    }
}
