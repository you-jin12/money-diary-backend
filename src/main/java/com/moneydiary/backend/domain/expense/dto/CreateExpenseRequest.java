package com.moneydiary.backend.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateExpenseRequest {

    @NotEmpty
    @Length(min = 1,max = 140)
    private String item;
    private int expenseMoney;
    private int incomeMoney;
    @Length(min = 0,max = 140)
    private String memo;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
    @NotNull
    private Long userId;

    public CreateExpenseRequest(String item, int expenseMoney, int incomeMoney, String memo, LocalDate expenseDate, Long userId) {
        this.item = item;
        this.expenseMoney = expenseMoney;
        this.incomeMoney = incomeMoney;
        this.memo = memo;
        this.expenseDate = expenseDate;
        this.userId = userId;
    }
}

/*{
        "item":"긴파치피규어",
        "expenseMoney":28400,
        "incomeMoney":0,
        "memo":"할인30%",
        "expenseDate":"2025-06-14",
        "userId":1
        }*/
