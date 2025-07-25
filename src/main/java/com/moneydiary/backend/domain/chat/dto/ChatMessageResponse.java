package com.moneydiary.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse implements ChatResponse{

    private String type;
    private Long chatId;

    private Long userId;
    private String nickName;
    private String userImg;
    private Long groupId;
    private String content; //지출내역엔 필요없음
    private LocalDateTime postDate;

    public  ChatMessageResponse(){}
    public ChatMessageResponse(Long chatId,Long userId, String nickName, String userImg, Long groupId, String content, LocalDateTime postDate) {
        this.type="ChatMessage";
        this.chatId=chatId;
        this.userId = userId;
        this.nickName = nickName;
        this.userImg = userImg;
        this.groupId = groupId;
        this.content = content;
        this.postDate = postDate;
    }

    public ChatMessageResponse(String content) {
        this.content = content;
    }

    @Override
    public LocalDateTime getPostDate() {
        return this.postDate;
    }
}
