package com.moneydiary.backend.common;

public interface REGEX {

    String ID_REGEX="^[a-z0-9!_,-]{8,16}$";
    String PW_REGEX="^[a-zA-Z0-9_!(),-]{8,16}$";
    String USERNAME_REGEX="^[가-힣]{2,10}$";
    String NICKNAME_REGEX="^[가-힣a-zA-Z0-9]{1,10}$";
}
