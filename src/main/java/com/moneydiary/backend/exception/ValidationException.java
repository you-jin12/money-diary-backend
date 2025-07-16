package com.moneydiary.backend.exception;

public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    @Override
    public String getMessage() {
        return "유효하지 않은 사용자입니다.";
    }
}
