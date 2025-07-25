package com.moneydiary.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.moneydiary.backend.domain.expense.Expense;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonTypeName(value="expenseChatMessageResponse")
@Getter
public class ExpenseChatMessageResponse implements ChatResponse{

    private String type;

    private Long chatId;
    private Long userId;
    private String nickName;
    private String userImg;
    private Long groupId;
    private String item;
    private int expenseMoney;
    private String memo;
    private LocalDate expenseDate;
    private LocalDateTime postDate;

    public ExpenseChatMessageResponse(){}
    public ExpenseChatMessageResponse(Long chatId,Long userId, String nickName, String userImg, Long groupId, String item, int expenseMoney, String memo, LocalDate expenseDate, LocalDateTime postDate) {
        this.type="ExpenseChatMessage";
        this.chatId=chatId;
        this.userId = userId;
        this.nickName = nickName;
        this.userImg = userImg;
        this.groupId = groupId;
        this.item = item;
        this.expenseMoney = expenseMoney;
        this.memo = memo;
        this.expenseDate = expenseDate;
        this.postDate = postDate;
    }

    public ExpenseChatMessageResponse(String item) {
        this.item = item;
    }

    @Override
    public LocalDateTime getPostDate() {
        return this.postDate;
    }
}
