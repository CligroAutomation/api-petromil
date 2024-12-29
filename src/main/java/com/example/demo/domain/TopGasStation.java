package com.example.demo.domain;

import com.example.demo.enums.TopType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Setter
@Getter
@Table(name = "topGasStation")
public class TopGasStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month", nullable = false)
    private String month; // Mes del reconocimiento (ej: "2024-11")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gas_station_id", nullable = false)
    private GasStation gasStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner; // Owner asociado destacado

    @Column(name = "performance_score")
    private Double performanceScore = 0.0; // Rendimiento del trabajador

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "bad_scores")
    private Integer badScores = 0;

    @Column(name = "comments_highlighted")
    private String commentsHighlighted = "";

    @Column(name = "top_type")
    private TopType topType;


    public TopGasStation(String month, GasStation gasStation, Owner owner, Double performanceScore, Double averageScore, Integer badScores, TopType topType) {
        this.month = month;
        this.gasStation = gasStation;
        this.owner = owner;
        this.performanceScore = performanceScore;
        this.averageScore = averageScore;
        this.badScores = badScores;
        this.commentsHighlighted = "";
        this.topType = topType;


    }

    public TopGasStation(){

    }
}
