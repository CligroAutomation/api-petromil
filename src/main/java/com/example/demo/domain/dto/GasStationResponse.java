package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({  "idGasStation","name", "address" })
public record GasStationResponse(Long idGasStation, String name, String address) {
}
