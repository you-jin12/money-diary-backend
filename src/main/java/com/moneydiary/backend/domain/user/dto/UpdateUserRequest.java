package com.moneydiary.backend.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UpdateUserRequest {

    @Pattern(regexp = "^$|^[가-힣a-zA-Z0-9]{1,10}$")
    private String nickName;
    @Pattern(regexp = "^$|^[a-zA-Z0-9_!(),-]{8,16}$")
    private String newPassword;
    private String newPasswordCheck;
    private String currentPassword;
    private boolean joinPolicy;
}


/*
{
        "nickName":"게론",
        "newPassword":"qwer1111",
        "newPasswordCheck":"qwer1111"
        ,"currentPassword":"qwer1234",
        "joinPolicy":"false"
        }
*/
