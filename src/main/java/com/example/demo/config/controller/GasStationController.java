package com.example.demo.config.controller;

import com.example.demo.dao.OwnerRepository;
import com.example.demo.domain.dto.*;
import com.example.demo.service.GasStationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<?> postGasStation(
            //@RequestBody @Valid GasStationResponse gasStationResponse,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("logo") MultipartFile logo,
            @RequestParam("banner") MultipartFile banner,
            @RequestParam("hexadecimalColor") String hexadecimalColor,
            @PathVariable Long idOwner) throws IOException {

        GasStationRequest request = new GasStationRequest(name, address);

        System.out.println("Entro al controlador post");

        GlobalSuccessResponse<?> response;
        GasStationResponse gasStation = gasStationService.createGasStation(request, idOwner, logo, banner, hexadecimalColor);

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera creado correctamente",
                gasStation);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{idOwner}/gasolineras")
    public ResponseEntity<?> getGasStationsByIdOwner(@PathVariable Long idOwner, Principal principal, Pageable pageable) {

        System.out.println("Valor de principal.getName(): " + principal.getName());

        Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

        // Verificar si el usuario es un ADMIN o si el OWNER está intentando acceder a
        // sus propias gasolineras
        if (!principal.getName().equals("admin@cligro.tech") && !authenticatedOwnerId.equals(idOwner)) {
            return new ResponseEntity<>(
                    new GlobalErrorResponse(false, "No tienes permiso para acceder a estas gasolineras"),
                    HttpStatus.FORBIDDEN);
        }

        GlobalSuccessResponse<?> response;
        List<GasStationsByOwnerResponse> gasStations = gasStationService.getGasStationByOwner(idOwner, pageable);

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolineras del usuario obtenidas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/gasolineras")
    public ResponseEntity<?> getAllGasStation(Pageable pageable) {

        List<GasStationsByOwnerResponse> gasStations = gasStationService.getAllGasStation(pageable);
        GlobalSuccessResponse<?> response;

        response = new GlobalSuccessResponse<>(
                true,
                "Lista de gasolineras encontradas correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/{idPropietario}/gasolineras/{idGasolinera}")
    public ResponseEntity<?> editGasStation(
            //@RequestBody @Valid GasStationResponse gasStationResponse,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("logo") MultipartFile logo,
            @RequestParam("banner") MultipartFile banner,
            @RequestParam("hexadecimalColor") String hexadecimalColor,
            @PathVariable Long idPropietario,
            @PathVariable Long idGasolinera, Principal principal) throws IOException {

        System.out.println("ENTRO AL CONTROLADOR PUT MAPPING GAS STATION");


        System.out.println("Valor de principal.getName(): " + principal.getName());
        Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());
        // Verificar si el usuario es un ADMIN o si el OWNER está intentando acceder a
        // sus propias gasolineras
        if (!principal.getName().equals("admin@cligro.tech") && !authenticatedOwnerId.equals(idPropietario)) {
            return new ResponseEntity<>(
                    new GlobalErrorResponse(false, "No tienes permiso para acceder a estas gasolineras"),
                    HttpStatus.FORBIDDEN);
        }

        System.out.println("Entrar al controlador");

        GasStationRequest gs = new GasStationRequest(name, address);
        GlobalSuccessResponse<?> response;
        GasStationsByOwnerResponse gasStations = gasStationService.updateGasStation(gs,logo, banner, hexadecimalColor, idPropietario,
                idGasolinera);

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera editada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/{idPropietario}/gasolineras/{idGasolinera}")
    public ResponseEntity<?> deleteGasStation(@PathVariable Long idPropietario, @PathVariable Long idGasolinera) {

        System.out.println("Controlador de delete");
        GlobalSuccessResponse<?> response;

        GasStationsByOwnerResponse gasStations = gasStationService.deleteGasStation(idPropietario, idGasolinera);
        System.out.println("GasStationsByOwnerResponse" + gasStations);

        response = new GlobalSuccessResponse<>(
                true,
                "Gasolinera eliminada correctamente",
                gasStations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
