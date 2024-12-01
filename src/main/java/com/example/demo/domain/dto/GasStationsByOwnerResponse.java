package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idGasStation", "name", "address", "ownerName"})
public record GasStationsByOwnerResponse(Long idGasStation, String name, String address, String ownerName) {
}
