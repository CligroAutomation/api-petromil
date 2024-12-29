package com.example.demo.domain.dto;

import com.example.demo.enums.TopType;

public record TopGasStationResponse(Long idTopGasStation, Double averageScore, Integer badScores,
                                    String commentsHighlighted, String month, Double performanceScore, Long idGasStation,
                                    TopType topType,
                                    Long idOwner) {
}
