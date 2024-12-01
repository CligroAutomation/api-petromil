package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idGasStation", "name", "address", "idOwner" })
public record GasStationResponse(Long idGasStation, String name, String address, Long idOwner) {
}
