package com.moneydiary.backend.domain.group.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupListResponse {

    private Long id;
    private String groupName;
    private String hostName;
    private int maxMember;
    private int currentMember;
    private String ago;
    private String hostImg;
    private boolean isPrivate;

    private LocalDate createDate;

    public GroupListResponse(Long id, String groupName, String hostName, int maxMember, int currentMember, String ago, String hostImg, LocalDate createDate,boolean isPrivate) {
        this.id = id;
        this.groupName = groupName;
        this.hostName = hostName;
        this.maxMember = maxMember;
        this.currentMember = currentMember;
        this.ago = ago;
        this.hostImg = hostImg;
        this.createDate=createDate;
        this.isPrivate=isPrivate;
    }
}
