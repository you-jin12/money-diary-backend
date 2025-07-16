package com.moneydiary.backend.domain.user;

import com.moneydiary.backend.common.FileUploadService;
import com.moneydiary.backend.domain.user.dto.CreateUserRequest;
import com.moneydiary.backend.domain.user.dto.LoginUserRequest;
import com.moneydiary.backend.domain.user.dto.SearchUserResponse;
import com.moneydiary.backend.domain.user.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;


    /**
     * 유저 로그인 로직
     * @param loginUserRequest
     * @return
     */
    public User findUser(LoginUserRequest loginUserRequest){
        User findUser = this.findByUserId(loginUserRequest.getUserId());
        checkPassword(findUser.getPassword(), loginUserRequest.getPassword());

        return findUser;
    }


    public void findAll(){
        List<User> all = userRepository.findAll();
        for (User user : all) {
            log.info("유저 아이디 : {}",user.getUserId());
        }
    }

    /**
     * 유저 회원가입 로직
     * @param createUserRequest
     */
    public void createUser(CreateUserRequest createUserRequest, MultipartFile multipartFile){
        String storeFilename="default.jpg";  //상수로
        if(multipartFile!=null){
            storeFilename = fileUploadService.storeFile(multipartFile);
        }

        User user = User.builder()
                .userId(createUserRequest.getUserId())
                .userName(createUserRequest.getUserName())
                .nickName(createUserRequest.getNickName())
                .password(createUserRequest.getPassword())
                .createDate(LocalDate.now())
                .joinPolicy(JoinPolicy.booleanToJoinPolicy(createUserRequest.isJoinPolicy()))
                .profileImg(storeFilename)
                .build();

        userRepository.save(user);
    }

    /**
     * 유저 회원탈퇴 로직
     * @param id
     */
    public void withdrawUser(Long id){
        User findUser = this.findById(id);
        userRepository.delete(findUser.getId());
    }

    /**
     * 유저 정보 수정 기능
     * 닉네임, 비밀번호, 그룹 가입 정책, 프로필 변경 가능
     * @param id
     * @param updateUserRequest
     */
    public void updateUser(Long id, UpdateUserRequest updateUserRequest,MultipartFile file){
        User findUser = this.findById(id);
        //비밀번호 인증 및 검증
        checkPassword(findUser.getPassword(), updateUserRequest.getCurrentPassword());
        //비밀번호 유효성검사
        isPasswordSame(updateUserRequest.getNewPassword(),updateUserRequest.getNewPasswordCheck());
        if(updateUserRequest.getNickName()!=null) findUser.updateUserNickName(updateUserRequest.getNickName());
        if(updateUserRequest.getNewPassword()!=null) findUser.updatePassword(updateUserRequest.getNewPassword());
        if(file!=null){
            String storeFilename = fileUploadService.storeFile(file);
            findUser.updateProfileImg(storeFilename);
        }
        findUser.updateJoinPolicy(JoinPolicy.booleanToJoinPolicy(updateUserRequest.isJoinPolicy()));
    }


    /**
     * 유저 아이디 중복 확인 기능
     * @param userId
     * @return
     * 중복 : false
     * 미중복 : true
     */
    public boolean isIdDuplication(String userId){
        return userRepository.findByUserId(userId).isEmpty();
    }

    //인증
    public void checkPassword(String findUserPassword, String requestPassword) {
        if(!findUserPassword.equals(requestPassword)){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    public User findByUserId(String userId){
        return userRepository.findByUserId(userId)
                .orElseThrow(()->new NoSuchElementException("유저를 찾을 수 없습니다."));
    }

    public User findById(Long id){
        User findUser = userRepository.findById(id);
        if(findUser==null){
            throw new RuntimeException("해당 유저가 존재하지 않습니다.");
        }
        return findUser;
    }

    //유효성검사
    public boolean isPasswordSame(String password,String passwordCheck){
        if (password != null) {
            if(password.equals(passwordCheck)){
                throw new RuntimeException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }
        }
        return true;
    }

    //키워드가 아이디나 닉네임에 포함된 유저 반환
    public List<SearchUserResponse> searchUser(String keyword){
        List<User> findUser = userRepository.findByKeyword(keyword);
        List<SearchUserResponse> userList=new ArrayList<>();
        for (User user : findUser) {
            userList.add(new SearchUserResponse(user.getId(), user.getUserId(), user.getNickName(), user.getUserGroupList().size(), user.getJoinPolicy().isJoinPolicy(), user.getProfileImg()));
        }
        return userList;
    }
}
