package com.example.demo.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthCreatedUserRequest(
        @NotBlank String identification,
        @NotBlank String email,
        @NotBlank String password,
        @Valid AuthCreateRoleRequest roleRequest
) {
}
