package com.moneydiary.backend.domain.usergroup;

import com.moneydiary.backend.domain.user.User;
import com.moneydiary.backend.domain.user.UserService;
import com.moneydiary.backend.domain.user.dto.UserSessionDTO;
import com.moneydiary.backend.domain.usergroup.dto.JoinGroupRequest;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/usergroups")
public class UserGroupController {
    private final UserGroupService userGroupService;
    private final UserService userService;

    //그룹 가입
    @PostMapping
    public ResponseEntity joinGroup(@SessionAttribute("user") UserSessionDTO session,
                                    @RequestBody JoinGroupRequest joinGroupRequest){
        log.info("유저아이디={}",joinGroupRequest.getUserId());
        userGroupService.joinGroup(session.getId(), joinGroupRequest.getUserId(),joinGroupRequest.getGroupId(),joinGroupRequest.getGroupPassword());
        //currentMember+1
        //채팅방 입장까지..소켓..
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,"그룹에 가입 되었습니다."));
    }

    //그룹탈퇴
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity withdrawGroup(@SessionAttribute("user")UserSessionDTO session,
                                        @PathVariable Long groupId,
                                        @PathVariable Long userId){
        userGroupService.deleteUserGroup(session.getId(),userId,groupId);
        log.info("========삭제후=========");
        User findUser = userService.findById(session.getId());
        List<UserGroup> userGroupList = findUser.getUserGroupList();
        for (UserGroup userGroup : userGroupList) {
            log.info("그룹 이름={} - 유저 닉네임={}",userGroup.getGroup().getGroupName(),userGroup.getUser().getNickName());
        }

        //소켓관련해서도 처리 필요?
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"그룹에서 탈퇴 되었습니다."));
    }


    //유저가 속한 그룹리스트
    @GetMapping("/users/{userId}/groups")
    public ResponseEntity getGroups(@SessionAttribute("user")UserSessionDTO session,
                                    @PathVariable Long userId){
        List groupList = userGroupService.getGroups(session.getId(), userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,groupList,"유저가 속한 그룹 리스트를 가져왔습니다."));
    }

    //그룹에 속한 유저 리스트
    @GetMapping("/groups/{groupId}/users")
    public ResponseEntity getUsers(@PathVariable Long groupId){
        List userList=userGroupService.getUsers(groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,userList,"그룹에 속한 유저 리스트를 가져왔습니다."));
    }
    //초대 수락 로직
    //초대 거부 로직
    //방장 양도 로직
}
