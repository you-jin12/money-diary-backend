package com.moneydiary.backend.domain.chat;

import com.moneydiary.backend.domain.usergroup.UserGroup;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Long id;
    @Lob
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_group_id")
    private UserGroup userGroup;
    private LocalDateTime postDate;
    private LocalDateTime updateDate;

    public ChatMessage() {
    }

    public ChatMessage(String content, UserGroup userGroup, LocalDateTime postDate) {
        this.content = content;
        this.userGroup = userGroup;
        this.postDate = postDate;
    }
}
