package com.example.demo.domain;

import com.example.demo.config.auditor.Auditable;
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
public class GasStation extends Auditable {

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

    @OneToMany(mappedBy = "gasStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Survey> surveys = new ArrayList<>();


    //Images

    @Column(name = "logo")
    private String logo = ""; // Atributo para almacenar la imagen

    @Column(name = "banner")
    private String banner = "";

    @Column(name = "hexadecimalColor")
    private String hexadecimalColor = "";

    // Constructor personalizado
    public GasStation(Long id, String name, String address, Owner owner, State state, String logo, String banner, String hexadecimalColor) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.state = state;
        this.logo = logo;
        this.banner = banner;
        this.hexadecimalColor = hexadecimalColor;


    }

    public GasStation(Long id, String name, String address, Owner owner, State state) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.state = state;

    }

}
