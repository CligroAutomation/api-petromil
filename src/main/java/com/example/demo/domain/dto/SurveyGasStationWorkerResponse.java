package com.example.demo.domain.dto;

import com.example.demo.enums.Rating;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyGasStationWorkerResponse(
        WorkerDTO worker,
        List<SurveyDTO> surveys
) {

    public record WorkerDTO(
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
