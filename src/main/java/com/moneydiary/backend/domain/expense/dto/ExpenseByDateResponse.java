package com.moneydiary.backend.domain.expense.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseByDateResponse {

    private Long id;
    private String item;
    private int expenseMoney;
    private int incomeMoney;
    private String memo;
    private LocalDate expenseDate;

    public ExpenseByDateResponse(Long id, String item, int expenseMoney, int incomeMoney, String memo, LocalDate expenseDate) {
        this.id = id;
        this.item = item;
        this.expenseMoney = expenseMoney;
        this.incomeMoney = incomeMoney;
        this.memo = memo;
        this.expenseDate = expenseDate;
    }
}
