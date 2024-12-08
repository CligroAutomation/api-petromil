package com.example.demo.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreatedUserRequest(
                @NotBlank String identification,
                @NotBlank String email,
                @NotBlank String password,
                @Valid AuthCreateRoleRequest roleRequest) {
}
