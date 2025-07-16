package com.moneydiary.backend.domain.expense.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateExpenseRequest {

    @Length(max=140)
    private String item;
    private int expenseMoney;
    private int incomeMoney;
    @Length(min=0,max=140)
    private String memo;
    private LocalDate expenseDate;

    public UpdateExpenseRequest(String item, int expenseMoney, int incomeMoney, String memo, LocalDate expenseDate) {
        this.item = item;
        this.expenseMoney = expenseMoney;
        this.incomeMoney = incomeMoney;
        this.memo = memo;
        this.expenseDate = expenseDate;
    }
}
