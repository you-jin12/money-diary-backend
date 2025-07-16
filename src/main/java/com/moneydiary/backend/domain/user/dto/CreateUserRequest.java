package com.moneydiary.backend.domain.user.dto;

import com.moneydiary.backend.common.REGEX;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateUserRequest {

    @NotBlank
    @Pattern(regexp = REGEX.ID_REGEX)
    private String userId;
    @NotBlank
    @Pattern(regexp = REGEX.USERNAME_REGEX)
    private String userName;
    @NotBlank
    @Pattern(regexp = REGEX.NICKNAME_REGEX)
    private String nickName;
    @NotBlank
    @Pattern(regexp = REGEX.PW_REGEX)
    private String password;
    private String passwordCheck;
//    private MultipartFile userImg;
    private boolean joinPolicy;
}

/*
{
    "userId":"hello123",
    "userName":"홍길동",
    "nickName":"gelong",
        "password":"qwer1234",
        "passwordCheck":"qwer1234",
        "joinPolicy":"true"
        }*/
