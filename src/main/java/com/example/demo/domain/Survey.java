package com.example.demo.domain;

import com.example.demo.enums.Rating;
import com.example.demo.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "surveys")
public class Survey {

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

    public Survey(){
        this.dateTime = LocalDateTime.now();
    }



}
