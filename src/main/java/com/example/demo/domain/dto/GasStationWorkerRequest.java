package com.example.demo.domain.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idGasStationWorker","identification", "name", "phone", "image", "idGasStation"})
public record GasStationWorkerRequest<T>(Long idGasStationWorker, String identification, String name, String phone, String image, Long idGasStation) {
}
