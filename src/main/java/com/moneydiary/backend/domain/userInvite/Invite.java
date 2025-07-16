package com.moneydiary.backend.domain.userInvite;

import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class Invite {

    @Id
    @Column(name = "invite_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="invite_user_id")
    private User inviteUser;

    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;
    private LocalDateTime inviteDate;

    @Builder
    public Invite(Long id, Group group, User user, User inviteUser, InvitationStatus invitationStatus, LocalDateTime inviteDate) {
        this.id = id;
        this.group = group;
        this.user = user;
        this.inviteUser = inviteUser;
        this.invitationStatus = invitationStatus;
        this.inviteDate = inviteDate;
    }

    public Invite() {
    }

    public void updateInvitationStatus(InvitationStatus invitationStatus){
        this.invitationStatus=invitationStatus;
    }
}
