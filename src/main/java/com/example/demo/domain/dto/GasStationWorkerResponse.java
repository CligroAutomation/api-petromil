package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idGasStationWorker", "identification", "name", "phone", "image", "gasStationName"})
public record GasStationWorkerResponse(Long idGasStationWorker, String identification, String name, String phone, String image, String gasStationName) {
}
