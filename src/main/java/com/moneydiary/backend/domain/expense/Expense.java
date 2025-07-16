package com.moneydiary.backend.domain.expense;

import com.moneydiary.backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="expense_id")
    private Long id;
    private String item;

    @Builder.Default
    private int incomeMoney=0;
    @Builder.Default
    private int expenseMoney=0;
    private String memo;
//    private LocalDateTime expenseDate; //지출이 발생한 날짜 및 시간(유저가 작성)
    private LocalDate expenseDate;
    private LocalDateTime createDate; // 유저가 지출 내역을 작성한 날짜(서버에서 할당)
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;


    @Builder
    public Expense(Long id, String item, int incomeMoney, int expenseMoney, String memo, LocalDate expenseDate, LocalDateTime createDate, LocalDateTime updateDate, User user) {
        this.id = id;
        this.item = item;
        this.incomeMoney = incomeMoney;
        this.expenseMoney = expenseMoney;
        this.memo = memo;
        this.expenseDate = expenseDate;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.user = user;
    }

    public Expense(){}

    public void updateItem(String item) {
        this.item = item;
    }

    public void updateIncomeMoney(int incomeMoney) {
        this.incomeMoney = incomeMoney;
    }

    public void updateExpenseMoney(int expenseMoney) {
        this.expenseMoney = expenseMoney;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
}
