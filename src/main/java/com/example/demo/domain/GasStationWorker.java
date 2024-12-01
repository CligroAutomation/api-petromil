package com.example.demo.domain;


import com.example.demo.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "gasStationWorkers")
public class GasStationWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Agregar la anotación @Version para control de versiones
    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name="identification", unique = true)
    @NotNull
    private String identification;


    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "phoneNumber", nullable = false)
    @NotNull
    private String phone;

    //@Lob // Esto indica que es un campo grande (como BLOB o CLOB en base de datos)
    @Column(name = "image")
    private String image; // Atributo para almacenar la imagen

    @Column(name = "state")
    private State state;

    // Relación N a 1 con GasStation
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "gas_station_id")
    private GasStation gasStation;



    public GasStationWorker(Long aLong, String identification, String name, String phone, String imageUrl, State state, GasStation gasStation) {
        this.id= aLong;
        this.identification = identification;
        this.name = name;
        this.phone = phone;
        this.image = imageUrl;
        this.state = state;
        this.gasStation = gasStation;
    }
}
