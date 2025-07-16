package com.moneydiary.backend.domain.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.ManyToOne;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class CreateGroupRequest {

    @Pattern(regexp = "^[A-Za-z0-9가-힣\\s]{2,16}$")
    private String groupName;
    @NotNull
    private Long groupHost;
    @Length(min=0,max=140)
    private String groupMemo;
    @Min(1)
    @Max(20)
    private int maxMember;
    @Min(1)
    @Max(20)
    private int currentMember; //바로가입멤버수만
    @NotNull
    @JsonProperty("isPrivate")
    private boolean isPrivate;
    private String groupPassword;
    private List<Long> groupMembers;
}
