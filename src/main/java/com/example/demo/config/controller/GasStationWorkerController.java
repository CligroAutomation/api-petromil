package com.example.demo.config.controller;

import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.domain.dto.GlobalErrorResponse;
import com.example.demo.domain.dto.GlobalSuccessResponse;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.GasStationWorkerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/gasolineras")
public class GasStationWorkerController {

    @Autowired
    private GasStationWorkerServiceImpl gasStationWorkerService;

    @Autowired
    private CloudinaryService cloudinaryService;


    @GetMapping("/trabajadores/{workerIdentification}")
    public ResponseEntity<?> getWorkerByIdentification(@PathVariable String workerIdentification) {

        GlobalSuccessResponse<?> response;
        GasStationWorkerResponse gasStationWorkerf = gasStationWorkerService.getGasStationWorkerById(workerIdentification);

        GlobalErrorResponse errorResponse;

        if (gasStationWorkerf == null) {
            errorResponse = new GlobalErrorResponse(
                    false,
                    "No existe este trabajador en la base de datos");

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Trabajador obtenido correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{idGasolinera}/trabajadores")
    public ResponseEntity<?> getWorkersByIdGasStation(@PathVariable Long idGasolinera) {

        System.out.println("Entró al controlador getWorkersByIdGasStation" );
        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        List<GasStationWorkerResponse> gasStationWorkerf = gasStationWorkerService
                .getAllWorkersByIdGasStation(idGasolinera);

        if (gasStationWorkerf == null) {
            errorResponse = new GlobalErrorResponse(
                    false,
                    "No hay trabajadores o la gasolinera no existe");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Trabajadores obtenidos correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @DeleteMapping("/{idGasolinera}/trabajadores/{idTrabajador}")
    public ResponseEntity<?> deleteWorkerById(@PathVariable Long idGasolinera, @PathVariable Long idTrabajador) {

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;
        GasStationWorkerResponse gasStationWorkerf = gasStationWorkerService.deleteGasStationWorkerById(idGasolinera,idTrabajador);

        if (gasStationWorkerf == null) {
            errorResponse = new GlobalErrorResponse(
                    false,
                    "No existe este trabajador (id) en la base de datos");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Trabajador eliminado correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/{idGasolinera}/trabajadores")
    public ResponseEntity<?> addWorkerWithImage(
            //@RequestParam("idGasStationWorker") Long idGasStationWorker,
            @RequestParam("identification") String identification,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            //@RequestParam("imageUrl") String imageUrl,
            //@RequestParam("idGasStation") Long idGasStation,
            @RequestParam("image") MultipartFile image, @PathVariable Long idGasolinera) throws IOException {

        System.out.println("Entro al controlador POSTMAPINNG");

        // Convertir el JSON a un objeto GasStationWorkerRequest

        GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(
                identification,
                name, phone);

        GasStationWorkerResponse worker = gasStationWorkerService.addWorkerWithImage(gasStationWorker, idGasolinera, image);
        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        if (worker == null) {
            errorResponse = new GlobalErrorResponse(
                    false,
                    "la gasolinera no existe o ya existe este trabajador en esa gasolinera");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Trabajador creado correctamente",
                worker);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/{idGasolinera}/trabajadores/{idTrabajador}")
    public ResponseEntity<?> editWorkerWithImage(
            //@RequestParam("idGasStationWorker") Long idGasStationWorker,
            @RequestParam("identification") String identification,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            //@RequestParam("imageUrl") String imageUrl,
            //@RequestParam("idGasStation") Long idGasStation,
            @RequestParam("image") MultipartFile image, @PathVariable Long idGasolinera, @PathVariable Long idTrabajador) throws IOException {

        System.out.println("Entro al controlador");

        // Convertir el JSON a un objeto GasStationWorkerRequest

        GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(
                identification,
                name, phone);

        GasStationWorkerResponse worker = gasStationWorkerService.updateGasStationWorkerWithImage(gasStationWorker,
                image, idGasolinera, idTrabajador);
        GlobalSuccessResponse<?> response;
        GlobalErrorResponse errorResponse;

        if (worker == null) {
            errorResponse = new GlobalErrorResponse(
                    false,
                    "No hay trabajadores o la gasolinera no existe o ya existe este trabajador en esa gasolinera");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        response = new GlobalSuccessResponse<>(
                true,
                "Trabajador editado correctamente",
                worker);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
