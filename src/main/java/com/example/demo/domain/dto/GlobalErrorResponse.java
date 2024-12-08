package com.example.demo.domain.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "success", "message"})
public record GlobalErrorResponse(boolean success, String message) {
}
