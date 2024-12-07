package com.example.demo.config.controller;

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

import java.util.List;

@RestController
@RequestMapping("/propietarios")
public class GasStationController {

    @Autowired
    private GasStationServiceImpl gasStationService;

    @PostMapping("/{idOwner}/gasolineras")
    public ResponseEntity<?> postGasStation(@RequestBody @Valid GasStationResponse gasStationResponse, @PathVariable Long idOwner) {

        System.out.println("Entro al controlador post");

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse globalErrorResponse;
        GasStationResponse gasStation = gasStationService.createGasStation(gasStationResponse, idOwner);

        if (gasStation == null) {
            globalErrorResponse = new GlobalErrorResponse(
                    false,
                    "Esta gasolinera existe y tienen un dueño asignado");

            return new ResponseEntity<>(globalErrorResponse, HttpStatus.BAD_REQUEST);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera creado correctamente",
                gasStation);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{idOwner}/gasolineras")
    public ResponseEntity<?> getGasStationsByIdOwner(@PathVariable Long idOwner) {

        GlobalSuccessResponse<?> response;
        List<GasStationsByOwnerResponse> gasStations = gasStationService.getGasStationByOwner(idOwner);
        GlobalErrorResponse globalErrorResponse;


        if (gasStations == null) {
            globalErrorResponse = new GlobalErrorResponse(
                    false,
                    "No hay gasolineras asociadas a este dueño");
            return new ResponseEntity<>(globalErrorResponse, HttpStatus.BAD_REQUEST);

        }

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
        GlobalErrorResponse globalErrorResponse;


        if (gasStations == null) {
            globalErrorResponse = new GlobalErrorResponse(
                    false,
                    "No existen gasolineras");
            return new ResponseEntity<>(globalErrorResponse, HttpStatus.NOT_FOUND);

        }

        response = new GlobalSuccessResponse<>(
                true,
                "Lista de gasolineras encontradas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/{idPropietario}/gasolineras/{idGasolinera}")
    public ResponseEntity<?> editGasStation(@RequestBody @Valid GasStationResponse gasStationResponse, @PathVariable Long idPropietario,@PathVariable Long idGasolinera ) {

        GlobalErrorResponse globalErrorResponse;
        System.out.println("Entrar al controlador");
        GlobalSuccessResponse<?> response;
        GasStationsByOwnerResponse gasStations = gasStationService.updateGasStation(gasStationResponse, idPropietario, idGasolinera);

        if (gasStations == null) {
            globalErrorResponse = new GlobalErrorResponse(
                    false,
                    "dueño inactivo o ya tiene la gasstation asociada O NO EXISTE GASOLINERA ");
            return new ResponseEntity<>(globalErrorResponse, HttpStatus.BAD_REQUEST);

        }

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
        GlobalErrorResponse globalErrorResponse;
        GasStationsByOwnerResponse gasStations = gasStationService.deleteGasStation(idPropietario,idGasolinera );
        System.out.println("GasStationsByOwnerResponse"+ gasStations);
        if (gasStations == null) {
            globalErrorResponse = new GlobalErrorResponse(
                    false,
                    "Esta gasolinera no existe");
            return new ResponseEntity<>(globalErrorResponse, HttpStatus.NOT_FOUND);

        }

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera eliminada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
