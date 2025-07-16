package com.moneydiary.backend.domain.expense.dto;

import lombok.Data;

@Data
public class DateExpense {

    private int date;
    private long totalExpense;

    public DateExpense(int date, long totalExpense) {
        this.date = date;
        this.totalExpense = totalExpense;
    }
}
