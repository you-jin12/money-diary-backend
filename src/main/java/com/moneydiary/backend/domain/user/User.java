package com.moneydiary.backend.domain.user;

import com.moneydiary.backend.domain.usergroup.UserGroup;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long id;
    @Column(name="id")
    private String userId;
    private String password;
    private String userName;
    private String nickName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;
    private LocalDate updateDate;
    @Builder.Default
    private String profileImg="default.jpg";
    @Enumerated(EnumType.STRING)
    private JoinPolicy joinPolicy=JoinPolicy.ACCEPT_INVITE;

    @OneToMany(mappedBy ="user" )
    private List<UserGroup> userGroupList=new ArrayList<>();


    public User() {
    }

    @Builder
    public User(Long id, String userId, String password, String userName, String nickName, LocalDate createDate, LocalDate updateDate, String profileImg, JoinPolicy joinPolicy) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.nickName = nickName;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.profileImg = profileImg;
        this.joinPolicy = joinPolicy;
    }

    public void updateUserNickName(String nickName){
        this.nickName=nickName;
    }

    public void updatePassword(String Password){
        this.password=password;
    }

    public void updateProfileImg(String profileImg){
        this.profileImg=profileImg;
    }

    public void updateJoinPolicy(JoinPolicy joinPolicy){
        this.joinPolicy=joinPolicy;
    }
}
