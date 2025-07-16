package com.moneydiary.backend.domain.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatResponse {

    private Long userId;
    private String nickName;
    private String userImg;
    private Long groupId;
    private String content;
    private LocalDateTime postDate;

    public ChatResponse(Long userId, String nickName, String userImg, Long groupId, String content, LocalDateTime postDate) {
        this.userId = userId;
        this.nickName = nickName;
        this.userImg = userImg;
        this.groupId = groupId;
        this.content = content;
        this.postDate = postDate;
    }
}
