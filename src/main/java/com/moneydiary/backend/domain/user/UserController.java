package com.moneydiary.backend.domain.user;

import com.moneydiary.backend.common.FileUploadService;
import com.moneydiary.backend.domain.group.dto.GroupListResponse;
import com.moneydiary.backend.domain.user.dto.*;
import com.moneydiary.backend.dto.ApiResponse;
import com.moneydiary.backend.common.REGEX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity signUp(@Valid @RequestPart(value="user") CreateUserRequest createUserRequest,
                                 @RequestPart(value="userProfile",required = false) MultipartFile file){

        userService.createUser(createUserRequest,file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,"회원가입에 성공했습니다."));
    }

    //로그인
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity login(@Valid @RequestBody LoginUserRequest loginUserRequest,
                                HttpServletRequest request){

        log.info("userId={}, password={}",loginUserRequest.getUserId(),loginUserRequest.getPassword());
        userService.findAll();
        //로그인 로직
        User findUser = userService.findUser(loginUserRequest);

        //로그인 성공
        UserSessionDTO userSessionDTO = UserSessionDTO.builder()
                .id(findUser.getId())
                .userId(findUser.getUserId())
                .userName(findUser.getUserName())
                .loginTime(LocalDateTime.now())
                .build();
        HttpSession session = request.getSession();
        session.setAttribute("user",userSessionDTO);
        LoginUserResponse loginUserResponse = new LoginUserResponse(findUser.getId(),findUser.getUserId(), findUser.getNickName(), findUser.getUserName(), findUser.getJoinPolicy().isJoinPolicy());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<LoginUserResponse>(true,loginUserResponse,"로그인에 성공했습니다."));
    }

    //회원탈퇴
    @DeleteMapping
    public ResponseEntity deleteUser(@SessionAttribute("user")UserSessionDTO session, HttpServletRequest request){
        Long userId = session.getId();
        userService.withdrawUser(userId);
        request.getSession(false).invalidate();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"회원 탈퇴에 성공했습니다."));
    }

    //회원 정보 수정
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> updateUser(@SessionAttribute("user")UserSessionDTO session,
                                                  @Valid @RequestPart UpdateUserRequest updateUserRequest,
                                                  @RequestPart(value="userProfile",required = false) MultipartFile file){


        log.info("회원 정보 수정");
        User findUser = userService.findById(session.getId());
        userService.updateUser(findUser.getId(), updateUserRequest,file);
        User afterUpdateUser = userService.findById(findUser.getId());

        //응답 세팅
        UpdateUserResponse updateUserResponse=new UpdateUserResponse(afterUpdateUser.getUserId(), afterUpdateUser.getUserName(), afterUpdateUser.getNickName(), afterUpdateUser.getJoinPolicy().isJoinPolicy(), afterUpdateUser.getProfileImg());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<UpdateUserResponse>(true,updateUserResponse,"유저 정보 수정에 성공했습니다."));
    }

    //아이디 중복 확인
    @GetMapping("/check")
    public ResponseEntity checkIdDuplication(@RequestParam String userId){
        log.info("아이디중복확인요청");
        if(!Pattern.matches(REGEX.ID_REGEX,userId)){
            return new ResponseEntity(new ApiResponse(false,"사용할 수 없는 아이디입니다."),HttpStatus.BAD_REQUEST);
        }

        boolean idDuplication = userService.isIdDuplication(userId);
        if(idDuplication){
            return new ResponseEntity(new ApiResponse(true,"사용할 수 있는 아이디입니다."),HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponse(false,"이미 사용 중인 아이디입니다."),HttpStatus.CONFLICT);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@SessionAttribute("user")UserSessionDTO session,HttpServletRequest request){
        User findUser = userService.findById(session.getId());
        request.getSession(false).invalidate();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true,"로그아웃에 성공했습니다."));
    }

    // 키워드가 닉네임이나 아이디에 들어가는 유저 리스트 반환
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List>> searchUser(@RequestParam("keyword") String keyword){
        List<SearchUserResponse> searchUserResponses = userService.searchUser(keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<List>(true,searchUserResponses,"검색하신 문구와 일치하는 유저 정보를 가져왔습니다."));
    }
}














