package com.example.demo.domain;

import com.example.demo.config.auditor.Auditable;
import com.example.demo.enums.Rating;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "surveys")
public class Survey extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_gasStationWorker", nullable = false)
    private GasStationWorker gasStationWorker;

    @ManyToOne
    @JoinColumn(name = "id_gasStation", nullable = false)
    private GasStation gasStation;

    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(length = 500) // Define un límite para el tamaño del comentario
    private String comment;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "electronicDevice", nullable = false)
    private String electronicDevice;

    public Survey() {
        this.dateTime = LocalDateTime.now();
    }

}
