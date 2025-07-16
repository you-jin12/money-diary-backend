package com.moneydiary.backend.domain.usergroup;

import com.moneydiary.backend.common.Validation;
import com.moneydiary.backend.domain.group.Group;
import com.moneydiary.backend.domain.group.GroupService;
import com.moneydiary.backend.domain.user.JoinPolicy;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import com.moneydiary.backend.domain.userInvite.InviteService;
import com.moneydiary.backend.domain.userInvite.dto.CreateInviteRequest;
import com.moneydiary.backend.domain.userInvite.dto.CreateInviteResponse;
import com.moneydiary.backend.domain.usergroup.dto.GroupsResponse;
import com.moneydiary.backend.domain.usergroup.dto.UsersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserGroupService {

    private final UserService userService;
    private final GroupService groupService;
    private final UserGroupRepository userGroupRepository;
    private final InviteService inviteService;
    private final Validation validation;

    /**
     * 그룹방 가입 신청 로직(유저 -> 그룹)
     * @param userId
     * @param groupId
     * @param password
     */
    public void joinGroup(Long sessionUserId,Long userId, Long groupId,String password) {
        User findUser = userService.findById(validation.validUser(userId, sessionUserId));
        Group findGroup = groupService.findById(groupId);

        //유저가 이미 3개의 그룹에 가입했으면 예외
        if(findUser.getUserGroupList().size()>=3){
            throw new RuntimeException("유저는 최대 3개의 그룹에 가입할 수 있습니다.");
        }
        //이미 가입된 그룹이면 예외
        for(UserGroup userGroup : findUser.getUserGroupList()){
            if(userGroup.getGroup().getId()==groupId){
                throw new RuntimeException("이미 가입된 그룹입니다.");
            }
        }
        //그룹에 멤버가 다 찼으면 예외
        if(findGroup.getMaxMember()==findGroup.getCurrentMember()){
            throw new RuntimeException("그룹 인원 수가 초과되어 참여할 수 없습니다.");
        }
        //그룹이 비공개 그룹일 경우, 비밀번호가 틀렸으면 예외
        if(findGroup.isPrivate() && !(findGroup.getGroupPassword().equals(password))){
            throw new RuntimeException("그룹의 비밀번호가 틀렸습니다.");
        }

        UserGroup userGroup = new UserGroup(findUser, findGroup, Role.MEMBER, LocalDate.now());
        findGroup.addCurrentMember();
        userGroupRepository.save(userGroup);
    }


    /**
     * 그룹 탈퇴 로직
     * @param userId
     * @param groupId
     */
    public void deleteUserGroup(Long sessionUserId,Long userId,Long groupId){
        User findUser = userService.findById(validation.validUser(userId, sessionUserId));
        Group findGroup = groupService.findById(groupId);

        List<UserGroup> userGroupList = findUser.getUserGroupList();
        log.info("========삭제전==========");
        for (UserGroup userGroup : userGroupList) {
            log.info("그룹 이름={} - 유저 닉네임={}",userGroup.getGroup().getGroupName(),userGroup.getUser().getNickName());
        }
        UserGroup findUserGroup = userGroupList.stream()
                .filter(userGroup -> userGroup.getGroup().getId() == groupId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 유저가 가입된 그룹이 아닙니다."));
        findUser.getUserGroupList().remove(findUserGroup);
        userGroupRepository.delete(findUserGroup.getId());
        findGroup.subCurrentMember();
    }

    /**
     * 그룹에 새로운 멤버 초대 or 추가 (그룹 -> 유저)
     * @param groupId
     * @param userId
     * @param hostSessionId
     * @param hostId
     */
    public CreateInviteResponse addNewMember(Long groupId, Long userId, Long hostSessionId, Long hostId){
        //올바른 요청인지 확인
        Long hostUserId = validation.validUser(hostSessionId, hostId);
        //요청을 보낸 유저가 호스트인지 확인
        Group findGroup = groupService.findById(groupId);
        if(findGroup.getGroupHost().getId()!=hostUserId){
            throw new RuntimeException("그룹 멤버 초대는 그룹의 방장만 가능합니다.");
        }
        //초대하려는 유저와 초대 하는 그룹이 존재하는지 확인
        User findUser = userService.findById(userId);
        //초대하려는 유저가 이미 3개의 그룹에 가입되어 있는지 검사
        if(findUser.getUserGroupList().size()>=3){
            throw new RuntimeException("유저가 이미 3개 이상의 그룹에 가입되어 있습니다.");
        }
        //초대하는 그룹의 최대 멤버 수 검사
        if(findGroup.getCurrentMember()>= findGroup.getMaxMember()){
            throw new RuntimeException("그룹 인원 수가 초과되어 초대할 수 없습니다.");
        }
        if(existUserGroupByUserIdAndGroupId(groupId,userId)){
            throw new RuntimeException("이미 그룹에 존재하는 유저입니다.");
        }
        //유저의 그룹방 가입 정책이 수락 후 가입이면
        if(findUser.getJoinPolicy()== JoinPolicy.ACCEPT_INVITE){
            inviteService.createInvite(findGroup,findUser,userService.findById(hostSessionId));
        }else{
            if(userId.equals(hostId)){
                throw new RuntimeException("자기 자신은 그룹에 추가할 수 없습니다.");
            }
            UserGroup userGroup = new UserGroup(findUser, findGroup, Role.MEMBER, LocalDate.now());
            userGroupRepository.save(userGroup);
            findGroup.addCurrentMember();
        }

        return new CreateInviteResponse(userId,findUser.getNickName(),findUser.getProfileImg());
    }

    public UserGroup findByUserIdAndGroupId(Long groupId,Long userId){
        UserGroup findUserGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if(findUserGroup==null){
            throw new RuntimeException("찾으시는 유저의 그룹 가입 정보가 없습니다.");
        }

        return findUserGroup;
    }

    public boolean existUserGroupByUserIdAndGroupId(Long groupId,Long userId){
        List<UserGroup> userGroupList = userGroupRepository.existUserGroupByUserIdAndGroupId(userId, groupId);
        return userGroupList.size()>0;
    }
    public List getGroups(Long sessionUserId, Long userId) {//페치조인?
        User findUser = userService.findById(validation.validUser(sessionUserId, userId));
        List<UserGroup> userGroupList = userGroupRepository.getGroups(findUser.getId());
        List<GroupsResponse> groupsResponseList=new ArrayList<>();
        for (UserGroup userGroup : userGroupList) {
            Group group = userGroup.getGroup();
            groupsResponseList.add(new GroupsResponse(group.getId(), group.getGroupName(),"","",group.getGroupImg()));
        }
        return groupsResponseList;
    }

    public List getUsers(Long groupId) {
        List<UserGroup> userGroupList = userGroupRepository.getUsers(groupId);
        List<UsersResponse> groupUserList=new ArrayList<>();
        for (UserGroup userGroup : userGroupList) {
            User user = userGroup.getUser();
            groupUserList.add(new UsersResponse(user.getId(),user.getNickName(),user.getProfileImg()));
        }
        return groupUserList;
    }
}
