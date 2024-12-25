package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "success", "message", "data", "total" })
public record GlobalSuccessResponse<T>(
        boolean success,
        String message,
        T data,
        @JsonInclude(JsonInclude.Include.NON_NULL) Number total) {

    // Constructor sin el campo total
    public GlobalSuccessResponse(boolean success, String message, T data) {
        this(success, message, data, null);
    }
}
