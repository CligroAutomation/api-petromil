package com.example.demo.domain.dto;

import com.example.demo.enums.Rating;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyGasStationResponse(
        GasStationDTO gasStationDTO,
        List<SurveyDTO> surveys
) {

    public record GasStationDTO(
            Long id,
            String name
    ) {}

    public record SurveyDTO(
            Long idSurvey,
            Rating rating,
            String comment,
            LocalDateTime localDateTime
    ) {}
}
