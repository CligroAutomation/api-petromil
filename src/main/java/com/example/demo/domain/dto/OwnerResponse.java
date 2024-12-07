package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idOwner", "identification", "name", "phone", "email", "password" })
public record OwnerResponse(Long idOwner, String identification, String name, String phone, String email,
        String password) {

}
