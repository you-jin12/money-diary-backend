package com.moneydiary.backend.exception;


import com.moneydiary.backend.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler
    public ResponseEntity UserValidationException(ValidationException e){
        log.error("validationException :", e.getMessage());
        return new ResponseEntity(new ApiResponse(false,e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
