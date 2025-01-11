package com.example.demo.config.controller;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.OwnerRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.dto.*;
import com.example.demo.service.SurveyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/encuestas")
public class SurveyController {

    @Autowired
    private SurveyServiceImpl surveyService;

    @Autowired
    private GasStationWorkerRepository gasStationWorkerRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @PostMapping
    public ResponseEntity<?> postSurvey(@RequestBody SurveyRequest surveyRequest) {
        System.out.println("PostMapping survey controller");

        GlobalSuccessResponse<?> response;

        SurveyRequest survey = surveyService.createSurvey(surveyRequest);

        response = new GlobalSuccessResponse<>(
                true,
                "Encuesta creada correctamente",
                survey);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/trabajadores/{idTrabajador}")
    public ResponseEntity<?> getSurveyByIdGasStationWorker(@PathVariable Long idTrabajador, Principal principal,
            Pageable pageable) {

        System.out.println("Controlador getSurveyByIdGasStationWorker ");

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        // Verificar si el usuario es ADMIN
        boolean isAdmin = principal.getName().equals("admin@cligro.tech");
        if (!isAdmin) {
            // Si no es ADMIN, verificar si el OWNER tiene permisos
            Optional<GasStationWorker> worker = gasStationWorkerRepository.findById(idTrabajador);

            if (worker.isEmpty()) {
                errorResponse = new GlobalErrorResponse(
                        false,
                        "No se encuentra la id del trabajador en la base de datos");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // Obtener la gasolinera asociada al trabajador
            GasStation gasStation = worker.get().getGasStation();

            // Obtener el ID del dueño autenticado
            Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

            // Verificar si la gasolinera pertenece al dueño autenticado
            if (!gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                errorResponse = new GlobalErrorResponse(
                        false,
                        "No tienes permiso para acceder a las encuestas de este trabajador");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }

        // Si es ADMIN o tiene permisos, obtener las encuestas del trabajador
        SurveyGasStationWorkerResponse survey = surveyService.getSurveyByIdGasStationWorker(idTrabajador, pageable);

        // Respuesta exitosa
        response = new GlobalSuccessResponse<>(
                true,
                "Lista de encuestas asignadas al trabajador obtenidas correctamente",
                survey);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/gasolineras/{idGasStation}")
    public ResponseEntity<?> getSurveyByIdGasStation(@PathVariable Long idGasStation, Principal principal,
            Pageable pageable) {

        System.out.println(pageable);
        System.out.println("Controlador getSurveyByIdGasStation");

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        // Verificar si el usuario es ADMIN
        boolean isAdmin = principal.getName().equals("admin@cligro.tech");
        if (!isAdmin) {
            // Si no es ADMIN, verificar si el OWNER tiene permisos
            Optional<GasStation> gasStationOptional = gasStationRepository.findById(idGasStation);

            if (gasStationOptional.isEmpty()) {
                errorResponse = new GlobalErrorResponse(
                        false,
                        "No se encuentra la id de la gasolinera en la base de datos");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // Obtener la gasolinera y verificar el propietario
            GasStation gasStation = gasStationOptional.get();
            Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

            if (!gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                errorResponse = new GlobalErrorResponse(
                        false,
                        "No tienes permiso para acceder a las encuestas de esta gasolinera");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }

        // Si es ADMIN o tiene permisos, obtener las encuestas de la gasolinera
        SurveyGasStationResponse survey = surveyService.getSurveyByIdGasStation(idGasStation, pageable);
        Long count = surveyRepository.countAllSurvey(idGasStation);
        // Respuesta exitosa
        response = new GlobalSuccessResponse<>(
                true,
                "Lista de encuestas asignadas a la gasolinera obtenidas correctamente",
                survey, count);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
