package com.moneydiary.backend.domain.chat;

import com.moneydiary.backend.domain.expense.Expense;
import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.reaction.ChatReaction;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="expense_chat_message")
public class ExpenseChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="expense_chat_message_id")
    private Long id;

    @OneToOne
    @JoinColumn(name="expense_id",unique = true)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name="user_group_id")
    private UserGroup userGroup;
    private LocalDateTime postDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "reaction")
    private List<ChatReaction> chatReactionList=new ArrayList<>();

    public ExpenseChatMessage(){}

    public ExpenseChatMessage(Expense expense,UserGroup userGroup, LocalDateTime postDate, LocalDateTime updateDate) {
        this.expense = expense;
        this.userGroup=userGroup;
        this.postDate = postDate;
        this.updateDate = updateDate;
    }
}
