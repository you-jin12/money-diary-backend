package com.moneydiary.backend.domain.userInvite;


import com.moneydiary.backend.common.Validation;
import com.moneydiary.backend.domain.user.dto.UserSessionDTO;
import com.moneydiary.backend.domain.userInvite.dto.*;
import com.moneydiary.backend.domain.usergroup.UserGroup;
import com.moneydiary.backend.domain.usergroup.UserGroupService;
import com.moneydiary.backend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invites")
@RequiredArgsConstructor
@Slf4j
public class InviteController {

    private final InviteService inviteService;
    private final UserGroupService userGroupService;
    private final Validation validation;

    //유저 초대 로직
    @PostMapping
    public ResponseEntity createInvite(@SessionAttribute("user")UserSessionDTO session,
                                       @RequestBody CreateInviteRequest request){
        CreateInviteResponse response = userGroupService.addNewMember(request.getGroupId(), request.getUserId(), session.getId(), request.getInviterId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<CreateInviteResponse>(true,response,"유저를 초대했습니다."));
    }

    //유저의 초대 리스트
    @GetMapping("/user/{userId}")
    public ResponseEntity getInvites(@SessionAttribute("user")UserSessionDTO session,
                                     @PathVariable("userId")Long userId){
        List<InviteResponse> inviteResponseList = inviteService.getInvitesByUserId(session.getId(), userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List<InviteResponse>>(true,inviteResponseList,"초대 알림 목록을 가져왔습니다."));

    }

    //초대 수락 or 거부 로직
    @PutMapping("/{inviteId}")
    public ResponseEntity updateInviteStatus(@SessionAttribute("user")UserSessionDTO session,
                                             @PathVariable("inviteId")Long inviteId,
                                             @RequestBody UpdateInviteStatusRequest updateInviteStatusRequest){
        log.info("userId={}",updateInviteStatusRequest.getUserId());
        validation.validUser(session.getId(), updateInviteStatusRequest.getUserId());
        inviteService.updateInviteStatus(inviteId,updateInviteStatusRequest);
        if(updateInviteStatusRequest.getInvitationStatus()==InvitationStatus.ACCEPTED){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true,"그룹에 가입 되셨습니다."));
        }else{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true,"그룹 참여를 거절 했습니다."));
        }
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity getInvitesByGroupId(@SessionAttribute("user")UserSessionDTO session,
                                              @PathVariable("groupId")Long groupId){
        List<GroupInviteResponse> inviteList = inviteService.getInvitesByGroupId(session.getId(), groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List<GroupInviteResponse>>(true,inviteList,"그룹의 초대 내역을 가져왔습니다."));
    }
}
