package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "success", "message", "data"})
public record GlobalSuccessResponse<T>(boolean success, String message, T data) {




}