package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.File;

@JsonPropertyOrder({"idGasStationWorker","identification", "name", "phone", "image", "idGasStation"})
public record GasStationWorkerResponseImg<T>(Long idGasStationWorker, String identification, String name, String phone, File image, T idGasStation) {
}
