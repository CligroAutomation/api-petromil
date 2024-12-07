package com.example.demo.config.controller;

import com.example.demo.domain.dto.*;
import com.example.demo.service.SurveyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encuestas")
public class SurveyController {

    @Autowired
    private SurveyServiceImpl surveyService;


    @PostMapping
    public ResponseEntity<?> postSurvey( @RequestBody SurveyRequest surveyRequest){
        System.out.println("PostMapping survey controller");

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        SurveyRequest survey = surveyService.createSurvey(surveyRequest);

        if(survey == null){

            errorResponse = new GlobalErrorResponse(
                    false,
                    "El usuario no puede ser creado porque ya existe o tiene el rol ADMIN asignado.");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        response = new GlobalSuccessResponse<>(
                true,
                "Encuesta creada correctamente",
                survey);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


    @GetMapping("/trabajadores/{idTrabajador}")
    public ResponseEntity<?> getSurveyByIdGasStationWorker (@PathVariable Long idTrabajador){

        System.out.println("Contralodor getSurveyByIdGasStationWorker ");

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;


        SurveyGasStationWorkerResponse survey = surveyService.getSurveyByIdGasStationWorker(idTrabajador);

        if(survey == null){

            errorResponse = new GlobalErrorResponse(
                    false,
                    "No se encuentra la id del trabajador en la base de datos o no tiene encuestas");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        response = new GlobalSuccessResponse<>(
                true,
                "Lista de encuestas asignadas al trabajador obtenidas correctamente",
                survey);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    @GetMapping("/gasolineras/{idGasStation}")
    public ResponseEntity<?> getSurveyByIdGasStation(@PathVariable Long idGasStation){


        System.out.println("Controlador getSurveyByIdGasStation");
        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;


        SurveyGasStationResponse survey = surveyService.getSurveyByIdGasStation(idGasStation);

        if(survey == null){

            errorResponse = new GlobalErrorResponse(
                    false,
                    "No se encuentra la id de la gasolinera en la base de datos o no tiene encuestas");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        response = new GlobalSuccessResponse<>(
                true,
                "Lista de encuestas asignadas a la gasolinera obtenidas correctamente",
                survey);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


}
