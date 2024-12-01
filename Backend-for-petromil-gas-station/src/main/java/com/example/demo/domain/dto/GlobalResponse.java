package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;



@JsonPropertyOrder({"success", "message", "data", "error"})
public record GlobalResponse<T>(
        boolean success,
        String message,
        T data,
        String error
) {
    public GlobalResponse(boolean success, String message, T data) {
        this(success, message, data, null); // Si hay `data`, no hay `error`
    }

    public GlobalResponse(boolean success, String message, String error) {
        this(success, message, null, error); // Si hay `error`, no hay `data`
    }

    // Constructor predeterminado simulando un constructor vacío
    public GlobalResponse() {
        this(false, null, null, null);
    }

}