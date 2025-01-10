package com.example.demo.domain.dto;

import com.example.demo.enums.TopType;

public record TopGasStationWorkerResponse(Long topGasStationWorkerid,
                                          Double averageScore,
                                          Integer badScores,
                                          String commentsHighlighted,
                                          Double performanceScore,
                                          Long idGasStation,
                                          Long gasStationWorkerId,
                                          TopType topType) {
}
