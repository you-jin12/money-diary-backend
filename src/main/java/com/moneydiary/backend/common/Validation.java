package com.moneydiary.backend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Validation {

    public Long validUser(Long sessionUserId,Long userId){
        log.info("sessionUserId={} || userId={}",sessionUserId,userId);
        if(!sessionUserId.equals(userId)){
            throw new RuntimeException("올바르지 않은 요청입니다.");
        }
        return sessionUserId;
    }
}
