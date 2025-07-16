package com.moneydiary.backend.domain.group.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlInlineBinaryData;

@Data
public class UpdateGroupRequest {

    @Pattern(regexp = "^$|^[A-Za-z0-9가-힣\\s]{2,16}$")
    private String groupName;
    @Length(min=0,max = 140)
    private String groupMemo;
    private Long groupHost;
    @Min(1)
    @Max(20)
    private int maxMember;
    @Min(1)
    @Max(20)
    private int currentMember;
    private boolean isPrivate;
    @Pattern(regexp = "^$|^[A-Za-z0-9]{4,8}$")
    private String groupPassword;
    @NotEmpty
    private Long userId;

}
