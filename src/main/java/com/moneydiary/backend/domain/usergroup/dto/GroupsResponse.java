package com.moneydiary.backend.domain.usergroup.dto;

import lombok.Data;

@Data
public class GroupsResponse {

    private Long id;
    private String groupName;
    private String lastChat;
    private String lastActiveTime;
    private String groupImg;


    public GroupsResponse(Long id, String groupName, String lastChat, String lastActiveTime, String groupImg) {
        this.id = id;
        this.groupName = groupName;
        this.lastChat = lastChat;
        this.lastActiveTime = lastActiveTime;
        this.groupImg = groupImg;
    }
}
