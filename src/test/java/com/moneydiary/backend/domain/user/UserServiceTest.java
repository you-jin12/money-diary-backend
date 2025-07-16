package com.moneydiary.backend.domain.user;

import com.moneydiary.backend.common.FileUploadService;
import com.moneydiary.backend.domain.user.dto.CreateUserRequest;
import com.moneydiary.backend.domain.user.dto.UpdateUserRequest;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.Pattern;
import java.io.File;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repository= mock(UserRepository.class);
    private FileUploadService fileUploadService=mock(FileUploadService.class);
    private UserService userService;

    @BeforeEach
    void beforeEach(){
        userService=new UserService(repository,fileUploadService);
    }

    @Test
    void 회원가입_파일없이(){
        //given
        CreateUserRequest request=new CreateUserRequest();
        request.setUserId("testUser");
        request.setUserName("테스트유저");
        request.setNickName("닉네임");
        request.setPassword("password123");
        request.setJoinPolicy(true);

        //when
        userService.createUser(request,null);
        //then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        User saveUser = userCaptor.getValue();

        assertThat("testUser").isEqualTo(saveUser.getUserId());
        assertThat("default.jpg").isEqualTo(saveUser.getProfileImg());
    }

    @Test
    void 회원가입_파일있음(){
        //given
        CreateUserRequest request=new CreateUserRequest();
        request.setUserId("testUser");
        request.setUserName("테스트유저");
        request.setNickName("닉네임");
        request.setPassword("password123");
        request.setJoinPolicy(false);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(fileUploadService.storeFile(mockFile)).thenReturn("storeFile.jpg");

        //when
        userService.createUser(request,mockFile);

        //then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertThat("storeFile.jpg").isEqualTo(user.getProfileImg());
        assertThat(JoinPolicy.ACCEPT_INVITE).isEqualTo(user.getJoinPolicy());

    }

    @Test
    void 회원수정_파일없음(){
//        //given
//        User mockUser=mock(User.class);
//        when(repository.findById(1L)).thenReturn(mockUser);
//
//        UpdateUserRequest request = new UpdateUserRequest();
//        request.setNickName("changenick");
//        request.setNewPassword("newpassword1");
//        request.setJoinPolicy(true);
//
//        //when
//        userService.updateUser(1L,request,null);
//
//        //then
//        assertThat("changenick").isEqualTo()
    }
}