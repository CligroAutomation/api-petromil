package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idGasStation", "name", "address", "ownerName", "ownerId", "logo", "banner", "hexadecimalColor" })
public record GasStationsByOwnerResponse(Long idGasStation, String name, String address, String ownerName,
                Long ownerId, String logo, String banner, String hexadecimalColor) {
}
