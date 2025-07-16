package com.moneydiary.backend.domain.group;

import com.moneydiary.backend.common.FileUploadService;
import com.moneydiary.backend.domain.group.dto.CreateGroupRequest;
import com.moneydiary.backend.domain.group.dto.UpdateGroupRequest;
import com.moneydiary.backend.domain.user.JoinPolicy;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import com.moneydiary.backend.domain.userInvite.InvitationStatus;
import com.moneydiary.backend.domain.userInvite.Invite;
import com.moneydiary.backend.domain.userInvite.InviteRepository;
import com.moneydiary.backend.domain.userInvite.InviteService;
import com.moneydiary.backend.domain.usergroup.Role;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import com.moneydiary.backend.domain.usergroup.UserGroupRepository;
import com.moneydiary.backend.domain.usergroup.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static com.moneydiary.backend.domain.userInvite.InvitationStatus.PENDING;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
//    private final InviteService inviteService;
    private final UserService userService;
    private final FileUploadService fileUploadService;


    /**
     * 그룹 생성 로직
     * @param request
     * @param multipartFile
     */
    public Long createGroup(Long sessionUserId,CreateGroupRequest request, MultipartFile multipartFile){

        Long hostId = validUser(sessionUserId, request.getGroupHost());
        if(request.isPrivate()){
            validPassword(request.getGroupPassword());
        }
        User groupHost = userService.findById(hostId);
        if(groupHost.getUserGroupList().size()>=3){
            throw new RuntimeException("유저가 이미 3개 이상의 그룹에 가입되어 있습니다.");
        }
        String storeFileName="default.jpg";
        if(multipartFile!=null){
            storeFileName = fileUploadService.storeFile(multipartFile);
        }
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .groupMemo(request.getGroupMemo())
                .maxMember(request.getMaxMember())
                .currentMember(1)
                .isPrivate(request.isPrivate())
                .GroupPassword(request.getGroupPassword())
                .groupImg(storeFileName)
                .groupHost(groupHost)
                .createDate(LocalDate.now())
                .build();
        groupRepository.save(group);
        //방장 유저그룹 테이블에 추가
        UserGroup userGroup = new UserGroup(groupHost, group, Role.HOST, LocalDate.now());
        userGroupRepository.save(userGroup);
        return group.getId();
    }

    //요청으로 들어온 유저 아이디와 세션의 유저 아이디가 일치하는지 확인하는 로직
    private Long validUser(Long sessionUserId, Long groupHost) {
        if(sessionUserId==groupHost){
            return sessionUserId;
        }else{
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
    }

    private void validPassword(String password) {
        if(!Pattern.matches("^[A-Za-z0-9]{4,8}$", password)){
            throw new RuntimeException("비공개 방일땐 비밀번호가 필수값 입니다."); // 빈 값이 아니라 정규식에 매칭 되지 않는 값이 올 수도 있는데?
        }
    }

    /**
     * 그룹 멤버 초대(방장 권한, 비공개방이어도 비번 필요 없음)
     * @param groupId
     * @param userId
     */
/*    public void addNewMember(Long groupId,Long userId,Long hostSessionId,Long hostId){
        //올바른 요청인지 확인
        //요청을 보낸 유저가 호스트인지 확인
        Group findGroup = this.findById(groupId);
        if(findGroup.getGroupHost().getId()!=validUser(hostSessionId, hostId)){
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
        //자기 자신을 초대하는지 이미 초대 했거나 그룹에 있는 멤버인지 등등 검사 필요?
        //유저의 그룹방 가입 정책이 수락 후 가입이면
        if(findUser.getJoinPolicy()== JoinPolicy.ACCEPT_INVITE){
            inviteService.createInvite(findGroup,findUser,userService.findById(hostSessionId));
        }else{
            UserGroup userGroup = new UserGroup(findUser, findGroup, Role.MEMBER, LocalDate.now());
            userGroupRepository.save(userGroup);
            findGroup.addCurrentMember();
        }
    }*/

    public Group findById(Long id){
        Group findGroup = groupRepository.findById(id);
        if(findGroup==null){
            throw new RuntimeException("그룹이 존재하지 않습니다.");
        }
        return findGroup;
    }

    public void updateGroup(Long sessionUserId, Long groupId, UpdateGroupRequest updateGroupRequest, MultipartFile multipartFile) {
        Long hostId = validUser(sessionUserId, updateGroupRequest.getGroupHost());
        Group findGroup = this.findById(groupId);
        if(findGroup.getGroupHost().getId()!=hostId){
            throw new RuntimeException("그룹 수정은 방장만 가능합니다.");
        }
        if(updateGroupRequest.isPrivate()){//비공개방
            if(findGroup.isPrivate()) {//원래 비공개방
                if(updateGroupRequest.getGroupPassword()!=null) findGroup.updateGroupPassword(updateGroupRequest.getGroupPassword());
            }else{ // 공개방 -> 비공개방 변경
                validPassword(updateGroupRequest.getGroupPassword());
                findGroup.updatePrivate(updateGroupRequest.isPrivate());
                findGroup.updateGroupPassword(updateGroupRequest.getGroupPassword());
            }
        }else{//공개방
            findGroup.updatePrivate(updateGroupRequest.isPrivate());
            findGroup.updateGroupPassword(null);
        }
        if(updateGroupRequest.getCurrentMember()> updateGroupRequest.getMaxMember()){
            throw new RuntimeException("그룹의 최대 멤버가 현재 멤버보다 작도록 수정할 수 없습니다.");
        }
        findGroup.updateMaxMember(updateGroupRequest.getMaxMember());
        if(!multipartFile.isEmpty()){
            String storeFileName = fileUploadService.storeFile(multipartFile);
            findGroup.updateGroupImg(storeFileName);
        }
        if(!updateGroupRequest.getGroupName().equals(findGroup.getGroupName())) findGroup.updateGroupName(updateGroupRequest.getGroupName());
        if(!updateGroupRequest.getGroupMemo().equals(findGroup.getGroupMemo())) findGroup.updateGroupMemo(updateGroupRequest.getGroupMemo());
    }

    /**
     * 그룹 삭제
     * @param groupId
     */
    public void deleteGroup(Long sessionUserId,Long groupId) {
        Group findGroup = this.findById(groupId);
        if(findGroup.getGroupHost().getId()!=sessionUserId){
            throw new RuntimeException("그룹 삭제는 방장만 가능합니다.");
        }
        groupRepository.delete(findGroup.getId());
        List<UserGroup> userGroupList = userGroupRepository.findByGroupId(findGroup.getId());
        for (UserGroup userGroup : userGroupList) {
            userGroupRepository.delete(userGroup.getId());
        }
    }

    public List<Group> getGroupList() {
        //최신순
        List<Group> groupList = groupRepository.getGroupList();
        return groupList;
    }
}
