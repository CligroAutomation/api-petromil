package com.example.demo.config.controller;

import com.example.demo.dao.OwnerRepository;
import com.example.demo.domain.dto.GasStationResponse;
import com.example.demo.domain.dto.GasStationsByOwnerResponse;
import com.example.demo.domain.dto.GlobalErrorResponse;
import com.example.demo.domain.dto.GlobalSuccessResponse;
import com.example.demo.service.GasStationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/propietarios")
public class GasStationController {

    @Autowired
    private GasStationServiceImpl gasStationService;

    @Autowired
    private OwnerRepository ownerRepository;


    @PostMapping("/{idOwner}/gasolineras")
    public ResponseEntity<?> postGasStation(@RequestBody @Valid GasStationResponse gasStationResponse, @PathVariable Long idOwner) {

        System.out.println("Entro al controlador post");

        GlobalSuccessResponse<?> response;
        GasStationResponse gasStation = gasStationService.createGasStation(gasStationResponse, idOwner);


        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera creado correctamente",
                gasStation);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{idOwner}/gasolineras")
    public ResponseEntity<?> getGasStationsByIdOwner(@PathVariable Long idOwner, Principal principal) {

        System.out.println("Valor de principal.getName(): " + principal.getName());

        Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

        // Verificar si el usuario es un ADMIN o si el OWNER está intentando acceder a sus propias gasolineras
        if (!principal.getName().equals("jgasparlopez29@gmail.com") && !authenticatedOwnerId.equals(idOwner)) {
            return new ResponseEntity<>(
                    new GlobalErrorResponse(false, "No tienes permiso para acceder a estas gasolineras"),
                    HttpStatus.FORBIDDEN
            );
        }

        GlobalSuccessResponse<?> response;
        List<GasStationsByOwnerResponse> gasStations = gasStationService.getGasStationByOwner(idOwner);


        response = new GlobalSuccessResponse<>(
                true,
                "Gasolineras del usuario obtenidas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/gasolineras")
    public ResponseEntity<?> getAllGasStation() {

        List<GasStationsByOwnerResponse> gasStations = gasStationService.getAllGasStation();
        GlobalSuccessResponse<?> response;





        response = new GlobalSuccessResponse<>(
                true,
                "Lista de gasolineras encontradas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/{idPropietario}/gasolineras/{idGasolinera}")
    public ResponseEntity<?> editGasStation(@RequestBody @Valid GasStationResponse gasStationResponse, @PathVariable Long idPropietario,@PathVariable Long idGasolinera, Principal principal ) {

        System.out.println("Valor de principal.getName(): " + principal.getName());
        Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());
        // Verificar si el usuario es un ADMIN o si el OWNER está intentando acceder a sus propias gasolineras
        if (!principal.getName().equals("jgasparlopez29@gmail.com") && !authenticatedOwnerId.equals(idPropietario)) {
            return new ResponseEntity<>(
                    new GlobalErrorResponse(false, "No tienes permiso para acceder a estas gasolineras"),
                    HttpStatus.FORBIDDEN
            );
        }


        System.out.println("Entrar al controlador");
        GlobalSuccessResponse<?> response;
        GasStationsByOwnerResponse gasStations = gasStationService.updateGasStation(gasStationResponse, idPropietario, idGasolinera);


        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera editada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/{idPropietario}/gasolineras/{idGasolinera}")
    public ResponseEntity<?> deleteGasStation(@PathVariable Long idPropietario, @PathVariable Long idGasolinera ) {

        System.out.println("Controlador de delete");
        GlobalSuccessResponse<?> response;

        GasStationsByOwnerResponse gasStations = gasStationService.deleteGasStation(idPropietario,idGasolinera );
        System.out.println("GasStationsByOwnerResponse"+ gasStations);

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera eliminada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
