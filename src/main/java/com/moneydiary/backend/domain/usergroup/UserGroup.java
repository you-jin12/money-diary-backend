package com.moneydiary.backend.domain.usergroup;

import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.user.User;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "user_group")
public class UserGroup {

    @Id
    @Column(name = "user_group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate joinDate;

    public UserGroup(){}

    //User의 리스트에도 값 추가
    public UserGroup(User user, Group group, Role role, LocalDate joinDate) {
        this.user = user;
        this.group = group;
        this.role = role;
        this.joinDate = joinDate;
        user.getUserGroupList().add(this);
    }
}
