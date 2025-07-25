package com.moneydiary.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;


public interface ChatResponse {
     LocalDateTime getPostDate();


}
