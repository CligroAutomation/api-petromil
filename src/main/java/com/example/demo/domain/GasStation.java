package com.example.demo.domain;


import com.example.demo.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gasstations")
public class GasStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    // Relación N a 1 con Dueno (Owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(name = "state")
    private State state;


    @OneToMany(mappedBy = "gasStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GasStationWorker> workers = new ArrayList<>();

    // Constructor personalizado
    public GasStation(Long id, String name, String address, Owner owner, State state) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.state = state;

    }

}
