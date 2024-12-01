package com.example.demo.config.controller;

import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.domain.dto.GlobalResponse;
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
@RequestMapping("/gas-station")
public class GasStationWorkerController {

    @Autowired
    private GasStationWorkerServiceImpl gasStationWorkerService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // @PostMapping("/create-worker")
    // public ResponseEntity<?> postGasStationWorker(@RequestBody @Valid
    // GasStationWorker gasStationWorker) {
    //
    // GlobalResponse<?> response;
    // GasStationWorkerResponse gasStationWorkerf =
    // gasStationWorkerService.addGasStationWorker(gasStationWorker);
    //
    // if(gasStationWorkerf == null){
    // response = new GlobalResponse<>(
    // false,
    // "No se creó este trabajador",
    // "Paso algo, no se pudo crear"
    // );
    // return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    // }
    // response = new GlobalResponse<>(
    // true,
    // "Trabajador creado correctamente",
    // gasStationWorkerf
    // );
    // return new ResponseEntity<>(response, HttpStatus.OK);
    //
    //
    // }

    @GetMapping("/get-worker-by-identification/{identification}")
    public ResponseEntity<?> getWorkerByIdentification(@PathVariable String identification) {

        GlobalResponse<?> response;
        GasStationWorkerResponse gasStationWorkerf = gasStationWorkerService.getGasStationWorkerById(identification);

        if (gasStationWorkerf == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existe este trabajador en la base de datos",
                    "identifiación inexistente");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response = new GlobalResponse<>(
                true,
                "Trabajador obtenido correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/get-AllWorkers-by-idGasStation/{idGasStation}")
    public ResponseEntity<?> getWorkerByIdentification(@PathVariable Long idGasStation) {

        GlobalResponse<?> response;
        List<GasStationWorkerResponse> gasStationWorkerf = gasStationWorkerService
                .getAllWorkersByIdGasStation(idGasStation);

        if (gasStationWorkerf == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existen trabajadores en esta gasolinera",
                    "No hay trabajadores o la gasolinera no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response = new GlobalResponse<>(
                true,
                "Trabajadores obtenidos correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // @PutMapping("/edit-worker")
    // public ResponseEntity<?> getWorkerByIdentification(@RequestBody @Valid
    // GasStationWorkerRequest gasStationWorkerRequest){
    //
    // GlobalResponse<?> response;
    // GasStationWorkerResponse gasStationWorkerf =
    // gasStationWorkerService.updateGasStationWorker(gasStationWorkerRequest);
    //
    // if(gasStationWorkerf == null){
    // response = new GlobalResponse<>(
    // false,
    // "No se pudo editar",
    // "El trabajador no existe en la base de datos"
    // );
    // return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    // }
    // response = new GlobalResponse<>(
    // true,
    // "Trabajador editado correctamente",
    // gasStationWorkerf
    // );
    // return new ResponseEntity<>(response, HttpStatus.OK);
    //
    // }

    @DeleteMapping("/delete-worker/{id}")
    public ResponseEntity<?> deleteWorkerById(@PathVariable Long id) {

        GlobalResponse<?> response;
        GasStationWorkerResponse gasStationWorkerf = gasStationWorkerService.deleteGasStationWorkerById(id);

        if (gasStationWorkerf == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existe este trabajador en la base de datos",
                    "ide no econtrada en base de datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response = new GlobalResponse<>(
                true,
                "Trabajador eliminado correctamente",
                gasStationWorkerf);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/create-worker-with-image")
    public ResponseEntity<?> addWorkerWithImage(
            @RequestParam("idGasStationWorker") Long idGasStationWorker,
            @RequestParam("identification") String identification,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam("idGasStation") Long idGasStation,
            @RequestParam("image") MultipartFile image) throws IOException {

        System.out.println("Entro al controlador");

        // Convertir el JSON a un objeto GasStationWorkerRequest

        GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(idGasStationWorker,
                identification,
                name, phone,
                imageUrl,
                idGasStation);

        GasStationWorkerResponse worker = gasStationWorkerService.addWorkerWithImage(gasStationWorker, image);
        GlobalResponse<?> response;

        if (worker == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existen trabajadores en esta gasolinera",
                    "No hay trabajadores o la gasolinera no existe o ya existe este trabajador en esa gasolinera");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response = new GlobalResponse<>(
                true,
                "Trabajador creado correctamente",
                worker);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/edit-worker-with-imagen")
    public ResponseEntity<?> editWorkerWithImage(
            @RequestParam("idGasStationWorker") Long idGasStationWorker,
            @RequestParam("identification") String identification,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam("idGasStation") Long idGasStation,
            @RequestParam("image") MultipartFile image) throws IOException {

        System.out.println("Entro al controlador");

        // Convertir el JSON a un objeto GasStationWorkerRequest

        GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(idGasStationWorker,
                identification,
                name, phone,
                imageUrl,
                idGasStation);

        GasStationWorkerResponse worker = gasStationWorkerService.updateGasStationWorkerWithImage(gasStationWorker,
                image);
        GlobalResponse<?> response;

        if (worker == null) {
            response = new GlobalResponse<>(
                    false,
                    "No existen trabajadores en esta gasolinera",
                    "No hay trabajadores o la gasolinera no existe o ya existe este trabajador en esa gasolinera");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response = new GlobalResponse<>(
                true,
                "Trabajador creado correctamente",
                worker);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
