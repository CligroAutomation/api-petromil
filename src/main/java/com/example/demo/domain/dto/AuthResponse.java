package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "token", "user" })
public record AuthResponse(String token, UserDTO user) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "id", "email", "role", "ownerId" })
    public static record UserDTO(Long id, String email, Role[] role, Long ownerId) {

        @JsonPropertyOrder("name")
        public static record Role(String name) {
        }
    }
}
