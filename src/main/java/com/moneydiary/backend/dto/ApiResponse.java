package com.moneydiary.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private T data;
    private String message;

    public ApiResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public ApiResponse(boolean isSuccess, T data, String message) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.message = message;
    }

    public ApiResponse() {
    }
}
