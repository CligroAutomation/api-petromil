package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "token", "user" })
public record AuthResponse(String token, UserDTO user) {

    @JsonPropertyOrder({ "id", "email", "role" })
    public static record UserDTO(Long id, String email, Role[] role) {

        @JsonPropertyOrder("name")
        public static record Role(String name) {
        }
    }
}
