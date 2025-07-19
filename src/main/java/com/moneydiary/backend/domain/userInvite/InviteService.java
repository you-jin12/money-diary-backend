package com.moneydiary.backend.domain.userInvite;

import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.group.GroupService;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import com.moneydiary.backend.domain.userInvite.dto.GroupInviteResponse;
import com.moneydiary.backend.domain.userInvite.dto.InviteResponse;
import com.moneydiary.backend.domain.userInvite.dto.UpdateInviteStatusRequest;
import com.moneydiary.backend.domain.usergroup.Role;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import com.moneydiary.backend.domain.usergroup.UserGroupRepository;
import com.moneydiary.backend.domain.usergroup.UserGroupService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.RequestingUserName;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final UserService userService;
    private final UserGroupRepository userGroupRepository;

    //이러면 createInvite를 호출하는 쪽에서 검증을 하고 요청을 해야함(그룹,유저 존재여부)
    public void createInvite(Group group,User user,User hostUser){
        List<UserGroup> userGroupList = user.getUserGroupList();
        //해당 멤버를 이미 초대했을 경우
        if(inviteRepository.findByUserIdAndGroupId(user.getId(), group.getId()).size()>0){
            throw new RuntimeException("이미 초대한 유저입니다.");
        }
        //해당 멤버가 이미 3개의 그룹에 속해 있을 경우
        if(userGroupList.size()>=3){
            throw new RuntimeException("유저가 이미 3개의 그룹에 가입되어 있습니다.");
        }
        //초대하려는 유저가 호스트가 아닌 경우
        List<UserGroup> hostUserGroupList = hostUser.getUserGroupList();
        for (UserGroup userGroup : hostUserGroupList) {
            if(userGroup.getGroup().getId().equals(group.getId())){//호스트 유저의 유저그룹 가져오기
                if(!userGroup.getRole().equals(Role.HOST)){
                    throw new RuntimeException("초대는 그룹의 방장만 할 수 있습니다.");
                }
            }
        }
        //자기 자신을 초대한 경우
        if(user.getId()== hostUser.getId()){
            throw new RuntimeException("자기 자신은 초대할 수 없습니다.");
        }

        //이미 그룹에 속해 있는 유저인 경우
        for (UserGroup userGroup : userGroupList) {
            if(userGroup.getGroup().getId().equals(group.getId())){
                throw new RuntimeException("이미 그룹에 속해 있는 유저입니다.");
            }
        }
        Invite invite= Invite.builder()
                .group(group)
                .user(user)
                .inviteUser(hostUser)
                .inviteDate(LocalDateTime.now())
                .invitationStatus(InvitationStatus.PENDING).build();
        inviteRepository.save(invite);
    }

    public List<InviteResponse> getInvitesByUserId(Long sessionUserId, Long userId) {
        User findUser = userService.findById(validUser(sessionUserId, userId));
        List<Invite> inviteList = inviteRepository.findByUser(findUser.getId());
        ArrayList<InviteResponse> inviteResponses = new ArrayList<>();
        for (Invite invite : inviteList) {
            if(invite.getInvitationStatus()==InvitationStatus.PENDING){
                inviteResponses.add(new InviteResponse(invite.getId(),invite.getGroup().getId(),invite.getGroup().getGroupName(),invite.getInviteUser().getId(),invite.getInviteUser().getNickName(),invite.getInviteDate()));
            }
        }
        return inviteResponses;
    }

    private Long validUser(Long sessionUserId,Long userId){
        if(sessionUserId==userId){
            return sessionUserId;
        }else{
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
    }


    /**
     * 초대 수락 or 거부 로직
     * @param inviteId
     * @param updateInviteStatusRequest
     */
    public void updateInviteStatus(Long inviteId,UpdateInviteStatusRequest updateInviteStatusRequest) {
        Invite findInvite = findById(inviteId);
        List<UserGroup> userGroupList = userGroupRepository.existUserGroupByUserIdAndGroupId(findInvite.getUser().getId(), findInvite.getGroup().getId());
        if(userGroupList.size()>0){
            throw new RuntimeException("이미 그룹에 가입되어 있습니다.");
        }
        //수락이면 => 상태 변경 후 userGroup에 추가
        if(updateInviteStatusRequest.getInvitationStatus()==InvitationStatus.ACCEPTED){
            Group findGroup = findInvite.getGroup();
            //그룹의 최대멤버를 초과하지 않는지 확인
            if(findGroup.getCurrentMember()>=findGroup.getMaxMember()){
                throw new RuntimeException("그룹이 정원을 초과하여 초대를 수락할 수 없습니다.");
            }
            findInvite.updateInvitationStatus(InvitationStatus.ACCEPTED);
            userGroupRepository.save(new UserGroup(findInvite.getUser(),findInvite.getGroup(),Role.MEMBER, LocalDate.now()));
            findGroup.addCurrentMember();
        } else if (updateInviteStatusRequest.getInvitationStatus()==InvitationStatus.REJECTED) {
            // 거절이면 => 상태 변경
            findInvite.updateInvitationStatus(InvitationStatus.REJECTED);
        }
    }

    public Invite findById(Long id){
        Invite findInvite = inviteRepository.findById(id);
        if(findInvite==null){
            throw new RuntimeException("존재하지 않는 초대 내역입니다.");
        }
        return findInvite;
    }

    /**
     * 그룹에서 초대한 유저 목록 (아직 초대를 수락하거나 거부 하지 않은 유저만)
     * @param userId
     * @param groupId
     * @return
     */
    public List<GroupInviteResponse> getInvitesByGroupId(Long userId, Long groupId) {
        User findUser = userService.findById(userId);
        UserGroup findUserGroup = userGroupRepository.findByUserIdAndGroupId(findUser.getId(), groupId);
        if(findUserGroup.getRole()!=Role.HOST){
            throw new RuntimeException("초대 목록 열람은 그룹의 방장만 가능합니다.");
        }
        List<Invite> inviteList = inviteRepository.findByGroup(groupId);
        ArrayList<GroupInviteResponse> groupInviteResponses = new ArrayList<>();
        for (Invite invite : inviteList) {
            User inviteUser = invite.getUser();
            if(invite.getInvitationStatus()== InvitationStatus.PENDING){
                groupInviteResponses.add(new GroupInviteResponse(invite.getId(),inviteUser.getId(),inviteUser.getNickName(),inviteUser.getProfileImg(),invite.getInviteDate()));
            }
        }
        return groupInviteResponses;
    }
}
