package com.example.demo.domain.dto;

import com.example.demo.enums.Rating;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idGasStationWorker", "idGasStation", "rating", "comment", "electronicDevice"})
public record SurveyRequest(Long idGasStationWorker, Long idGasStation, Rating rating, String comment, String electronicDevice) {


}
