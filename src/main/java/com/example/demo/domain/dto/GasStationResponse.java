package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idGasStation", "name", "address", "logo", "banner", "hexadecimalColor", "ownerId" })
public record GasStationResponse(Long idGasStation, String name, String address, String logo, String banner, String hexadecimalColor, Long ownerId) {
}
