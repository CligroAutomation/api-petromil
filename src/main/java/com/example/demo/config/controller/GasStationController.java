package com.example.demo.config.controller;

import com.example.demo.domain.dto.GasStationResponse;
import com.example.demo.domain.dto.GasStationsByOwnerResponse;
import com.example.demo.domain.dto.GlobalResponse;
import com.example.demo.service.GasStationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
public class GasStationController {

    @Autowired
    private GasStationServiceImpl gasStationService;

    @PostMapping("/create-gasStation")
    public ResponseEntity<?> postGasStation(@RequestBody @Valid GasStationResponse gasStationResponse) {

        System.out.println("Entro al controlador");

        GlobalResponse<?> response;
        GasStationResponse gasStation = gasStationService.createGasStation(gasStationResponse);

        if (gasStation == null) {
            response = new GlobalResponse<>(
                    false,
                    "Esta gasolinera existe y tienen un dueño asignado",
                    "Se intentó agregar dos veces a un dueño la misma gasolinera");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response = new GlobalResponse<>(
                true,
                "Gasolinera creado correctamente",
                gasStation);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/get-gas-stations-by-owner/{id}")
    public ResponseEntity<?> getGasStationsByIdOwner(@PathVariable Long id) {

        GlobalResponse<?> response;
        List<GasStationsByOwnerResponse> gasStations = gasStationService.getGasStationByOwner(id);

        if (gasStations == null) {
            response = new GlobalResponse<>(
                    false,
                    "No hay gasolineras asociadas a este dueño",
                    "Este dueño no existe o sí existe pero no tiene gasolineras");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }

        response = new GlobalResponse<>(
                true,
                "Gasolineras del usuario obtenidas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/get-all-gas-stations")
    public ResponseEntity<?> getAllGasStation() {

        List<GasStationsByOwnerResponse> gasStations = gasStationService.getAllGasStation();
        GlobalResponse<?> response;

        if (gasStations == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existen gasolineras",
                    "No hay gasolineras en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }

        response = new GlobalResponse<>(
                true,
                "Lista de gasolineras encontradas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/edit-gas-station")
    public ResponseEntity<?> editGasStation(@RequestBody @Valid GasStationResponse gasStationResponse) {

        GlobalResponse<?> response;
        GasStationsByOwnerResponse gasStations = gasStationService.updateGasStation(gasStationResponse);

        if (gasStations == null) {
            response = new GlobalResponse<>(
                    false,
                    "dueño inactivo o ya tiene la gasstation asociada ",
                    "dueño inactivo o ya tiene la gasstation asociada");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }

        response = new GlobalResponse<>(
                true,
                "Gasolinera editada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/delete-gas-station/{idGasStation}")
    public ResponseEntity<?> deleteGasStation(@PathVariable Long idGasStation) {

        GlobalResponse<?> response;
        GasStationsByOwnerResponse gasStations = gasStationService.deleteGasStation(idGasStation);

        if (gasStations == null) {
            response = new GlobalResponse<>(
                    false,
                    "Esta gasolinera no existe",
                    "No se encontró es id de gasolinera en la base de daos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }

        response = new GlobalResponse<>(
                true,
                "Gasolinera eliminada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
