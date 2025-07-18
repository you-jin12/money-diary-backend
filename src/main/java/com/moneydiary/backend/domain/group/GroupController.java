package com.moneydiary.backend.domain.group;

import com.moneydiary.backend.domain.group.dto.CreateGroupRequest;
import com.moneydiary.backend.domain.group.dto.GroupListResponse;
import com.moneydiary.backend.domain.group.dto.GroupResponse;
import com.moneydiary.backend.domain.group.dto.UpdateGroupRequest;
import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import com.moneydiary.backend.domain.user.dto.UserSessionDTO;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import com.moneydiary.backend.domain.usergroup.UserGroupService;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserGroupService userGroupService;

    //그룹 생성
    @PostMapping
    public ResponseEntity createGroup(@SessionAttribute("user") UserSessionDTO session,
                                      @Valid @RequestPart(value="group") CreateGroupRequest createGroupRequest,
                                      @RequestPart(value = "groupImg",required = false) MultipartFile multipartFile){
        Long groupId = groupService.createGroup(session.getId(),createGroupRequest, multipartFile);

        //currentMember를 1로 설정하지 않으면 멤버 추가로직 에러 발생시(롤백) 데이터가 안맞음 (현재멤버는 세팅 되어있지만 유저그룹은 세팅ㅌ)
        List<Long> groupMembers = createGroupRequest.getGroupMembers();
//        groupMembers.remove(createGroupRequest.getGroupHost());
        //방장 제외 멤버들 추가
        for (Long userId : groupMembers) {
            userGroupService.addNewMember(groupId,userId,session.getId(),createGroupRequest.getGroupHost());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,groupId,"그룹을 만들었습니다."));
    }

    //그룹 리스트
    @GetMapping
    public ResponseEntity getGroupList(){
        //최신순 : 가장 최근에 만들어진 방 상위 100개(default)
        //활동순 : 가장 최근에 채팅이 올라온 방 상위 100개
        List<Group> result = groupService.getGroupList();
        List<GroupListResponse> groupList=new ArrayList<>();
        for (Group group : result) {
            GroupListResponse groupListResponse = new GroupListResponse(group.getId(), group.getGroupName(), group.getGroupHost().getNickName(),
                    group.getMaxMember(), group.getCurrentMember(), "", group.getGroupImg(),group.getCreateDate(),group.isPrivate());
            groupList.add(groupListResponse);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List>(true,groupList,"그룹 리스트를 가져왔습니다."));
    }

    //그룹 정보
    @GetMapping("/{groupId}")
    public ResponseEntity getGroup(@PathVariable Long groupId){
        Group findGroup = groupService.findById(groupId);
        GroupResponse groupResponse=new GroupResponse(findGroup.getId(), findGroup.getGroupName(),findGroup.getGroupHost().getId(),findGroup.getGroupHost().getNickName(),
                findGroup.getMaxMember(),findGroup.getCurrentMember(),findGroup.getGoal(), findGroup.getGroupMemo(), findGroup.getGroupHost().getProfileImg(), findGroup.isPrivate());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,groupResponse,"그룹 정보를 가져왔습니다."));
    }

    //그룹 수정(방장만 가능)
    @PutMapping("/{groupId}")
    public ResponseEntity updateGroup(@SessionAttribute("user")UserSessionDTO session,
                                      @PathVariable Long groupId,
                                      @Valid @RequestPart UpdateGroupRequest updateGroupRequest,
                                      @RequestPart(value = "groupImg",required = false) MultipartFile multipartFile){

        groupService.updateGroup(session.getId(),groupId,updateGroupRequest,multipartFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"그룹 정보를 수정했습니다."));
    }

    @PostMapping("/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable Long groupId,
                                      @SessionAttribute("user")UserSessionDTO session){
        groupService.deleteGroup(session.getId(),groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"그룹을 삭제 했습니다."));
    }


}
