package com.moneydiary.backend.domain.group;


import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import lombok.Builder;
import lombok.Getter;
import net.bytebuddy.asm.Advice;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="chat_group")
public class Group {

    @Id
    @Column(name="group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String groupName;
    private int maxMember;
    private int currentMember;
    private String groupMemo;
    private boolean isPrivate;
    private String GroupPassword;
    private String groupImg;
    private String goal; // 테이블 따로 만들어야할듯 구체화 필요
    private LocalDate createDate;

    private LocalDate updateDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User groupHost;

    @OneToMany(mappedBy ="group")
    private List<UserGroup> userGroupList=new ArrayList<>();

    public Group(){}

    @Builder
    public Group(Long id, String groupName, int maxMember, int currentMember, String groupMemo, boolean isPrivate, String GroupPassword, String groupImg, String goal, LocalDate createDate, User groupHost) {
        this.id = id;
        this.groupName = groupName;
        this.maxMember = maxMember;
        this.currentMember = currentMember;
        this.groupMemo = groupMemo;
        this.isPrivate = isPrivate;
        this.GroupPassword = GroupPassword;
        this.groupImg = groupImg;
        this.goal = goal;
        this.createDate = createDate;
        this.groupHost = groupHost;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void updateMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public void updateGroupMemo(String groupMemo) {
        this.groupMemo = groupMemo;
    }

    public void updatePrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void updateGroupPassword(String groupPassword) {
        GroupPassword = groupPassword;
    }

    public void updateGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public void updateUpdateDate(LocalDate updateDate){
        this.updateDate=updateDate;
    }

    public void changeGroupHost(User groupHost){
        this.groupHost=groupHost;
    }

    public void subCurrentMember(){
        this.currentMember--;
    }

    public void addCurrentMember(){
        this.currentMember++;
    }

}
