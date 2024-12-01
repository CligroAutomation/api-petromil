package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NoArgsConstructor;


@JsonPropertyOrder({"idOwner","identification", "nombre", "telefono", "email", "password"})
public record OwnerResponse(Long idOwner,String identification, String nombre, String telefono, String email, String password) {




}
