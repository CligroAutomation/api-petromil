package com.example.demo.domain.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"identification", "name", "phone"})
public record GasStationWorkerRequest<T>(String identification, String name, String phone) {
}
