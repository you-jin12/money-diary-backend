package com.moneydiary.backend.domain.user;

import lombok.Getter;

@Getter
public enum JoinPolicy {
    ACCEPT_INVITE(false),
    ACCEPT_CONFIRM(true);

    private final boolean joinPolicy;

    JoinPolicy(boolean joinPolicy) {
        this.joinPolicy = joinPolicy;
    }

    public static JoinPolicy booleanToJoinPolicy(boolean joinPolicy){
        if(joinPolicy){
            return ACCEPT_CONFIRM;
        }else{
            return ACCEPT_INVITE;
        }
    }
}
