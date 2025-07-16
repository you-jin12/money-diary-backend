package com.moneydiary.backend.domain.user.dto;

import com.moneydiary.backend.common.REGEX;
import lombok.Data;

import javax.validation.constraints.Pattern;


@Data
public class LoginUserRequest {

    @Pattern(regexp = REGEX.ID_REGEX, message = "유효하지 않은 아이디입니다!")
    String userId;


    @Pattern(regexp = REGEX.PW_REGEX,message = "비밀번호 형식에 맞지 않습니다!")
    String password;

}
