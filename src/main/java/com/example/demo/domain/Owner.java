package com.example.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "phoneNumber", nullable = false)
    @NotNull
    private String phone;

    // Relación 1 a N con GasStation
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<GasStation> gasStations = new HashSet<>();

}
