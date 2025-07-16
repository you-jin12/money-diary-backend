package com.moneydiary.backend.domain.user.dto;

import lombok.Data;

@Data
public class SearchUserResponse {

    private Long id;
    private String userId;
    private String nickName;
    private int joinedGroupCount;
    private boolean joinPolicy;
    private String userImg;

    public SearchUserResponse(Long id, String userId, String nickName, int joinedGroupCount, boolean joinPolicy, String userImg) {
        this.id = id;
        this.userId = userId;
        this.nickName = nickName;
        this.joinedGroupCount = joinedGroupCount;
        this.joinPolicy = joinPolicy;
        this.userImg = userImg;
    }
}
