package com.moneydiary.backend.domain.chat.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private Long userId;
    private Long groupId;
    private String message;
}
