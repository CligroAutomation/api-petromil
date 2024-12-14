package com.example.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"idGasStation","name", "address"})
public record GasStationRequest(String name, String address) {





}


