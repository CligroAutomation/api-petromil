package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "token", "user" })
public record AuthResponse(String token, UserDTO usuario) {

    @JsonPropertyOrder({ "id", "email" })
    public static record UserDTO(Long id, String email) {
    }
}
