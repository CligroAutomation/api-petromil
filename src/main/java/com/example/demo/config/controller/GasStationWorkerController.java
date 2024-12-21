package com.example.demo.config.controller;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.OwnerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.domain.dto.GlobalErrorResponse;
import com.example.demo.domain.dto.GlobalSuccessResponse;
import com.example.demo.service.GasStationWorkerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gasolineras")
public class GasStationWorkerController {

        @Autowired
        private GasStationWorkerServiceImpl gasStationWorkerService;

        @Autowired
        private OwnerRepository ownerRepository;

        @Autowired
        private GasStationRepository gasStationRepository;

        @Autowired
        private GasStationWorkerRepository gasStationWorkerRepository;

        @GetMapping("/trabajadores/{workerIdentification}")
        public ResponseEntity<?> getWorkerByIdentification(@PathVariable String workerIdentification,
                        Principal principal) {

                Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

                // Verificar si el usuario es ADMIN
                boolean isAdmin = principal.getName().equals("admin@cligro.tech");
                if (!isAdmin) {
                        // Si no es ADMIN, verificar si el OWNER está intentando acceder solo a sus
                        // propios trabajadores
                        // Buscar el trabajador por su identificación
                        Optional<GasStationWorker> worker = gasStationWorkerRepository
                                        .findByIdentification(workerIdentification);

                        if (worker.isEmpty()) {
                                return new ResponseEntity<>(
                                                new GlobalErrorResponse(false,
                                                                "No existe este trabajador en la base de datos"),
                                                HttpStatus.NOT_FOUND);
                        }

                        // Obtener la gasolinera asociada al trabajador
                        GasStationWorker gsw = worker.get();
                        GasStation gasStation = gsw.getGasStation();

                        // Verificar si la gasolinera del trabajador pertenece al dueño autenticado
                        if (!gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                                return new ResponseEntity<>(
                                                new GlobalErrorResponse(false,
                                                                "No tienes permiso para acceder a los datos de este trabajador"),
                                                HttpStatus.FORBIDDEN);
                        }
                }

                // Si el usuario es ADMIN o tiene permisos, devolver la información del
                // trabajador
                GasStationWorkerResponse gasStationWorkerResponse = gasStationWorkerService
                                .getGasStationWorkerById(workerIdentification);

                // Respuesta exitosa
                return new ResponseEntity<>(
                                new GlobalSuccessResponse<>(true, "Trabajador obtenido correctamente",
                                                gasStationWorkerResponse),
                                HttpStatus.OK);

        }

        @GetMapping("/{idGasolinera}/trabajadores")
        public ResponseEntity<?> getWorkersByIdGasStation(@PathVariable Long idGasolinera, Pageable pageable) {

                System.out.println("Entró al controlador getWorkersByIdGasStation");
                GlobalSuccessResponse<?> response;

                List<GasStationWorkerResponse> gasStationWorkerf = gasStationWorkerService
                                .getAllWorkersByIdGasStation(idGasolinera, pageable);

                response = new GlobalSuccessResponse<>(
                                true,
                                "Trabajadores obtenidos correctamente",
                                gasStationWorkerf);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @DeleteMapping("/{idGasolinera}/trabajadores/{idTrabajador}")
        public ResponseEntity<?> deleteWorkerById(@PathVariable Long idGasolinera, @PathVariable Long idTrabajador,
                        Principal principal) {

                Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

                // Verificar si el usuario es ADMIN o el propietario de la gasolinera
                boolean isAdmin = principal.getName().equals("admin@cligro.tech");
                if (!isAdmin) {
                        // Verificar si el OWNER está intentando acceder a una gasolinera que le
                        // pertenece
                        GasStation gasStation = gasStationRepository.findById(idGasolinera)
                                        .orElse(null);

                        if (gasStation == null || !gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                                return new ResponseEntity<>(
                                                new GlobalErrorResponse(false,
                                                                "No tienes permiso para eliminar trabajadores de esta gasolinera"),
                                                HttpStatus.FORBIDDEN);
                        }
                }

                GlobalSuccessResponse<?> response;

                GasStationWorkerResponse gasStationWorkerf = gasStationWorkerService
                                .deleteGasStationWorkerById(idGasolinera, idTrabajador);

                response = new GlobalSuccessResponse<>(
                                true,
                                "Trabajador eliminado correctamente",
                                gasStationWorkerf);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @PostMapping("/{idGasolinera}/trabajadores")
        public ResponseEntity<?> addWorkerWithImage(
                        @RequestParam("identification") String identification,
                        @RequestParam("name") String name,
                        @RequestParam("phone") String phone,
                        @RequestParam("image") MultipartFile image, @PathVariable Long idGasolinera,
                        Principal principal) throws IOException {

                System.out.println("Entro al controlador POSTMAPINNG");

                Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

                // Verificar si el usuario es ADMIN o el propietario de la gasolinera
                boolean isAdmin = principal.getName().equals("admin@cligro.tech");
                if (!isAdmin) {
                        // Verificar si el OWNER está intentando acceder a una gasolinera que le
                        // pertenece
                        GasStation gasStation = gasStationRepository.findById(idGasolinera)
                                        .orElse(null);

                        if (gasStation == null || !gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                                return new ResponseEntity<>(
                                                new GlobalErrorResponse(false,
                                                                "No tienes permiso para asignar trabajadores a esta gasolinera"),
                                                HttpStatus.FORBIDDEN);
                        }
                }

                // Convertir el JSON a un objeto GasStationWorkerRequest

                @SuppressWarnings("rawtypes")
                GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(
                                identification,
                                name, phone);

                GasStationWorkerResponse worker = gasStationWorkerService.addWorkerWithImage(gasStationWorker,
                                idGasolinera, image);
                GlobalSuccessResponse<?> response;

                response = new GlobalSuccessResponse<>(
                                true,
                                "Trabajador creado correctamente",
                                worker);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @PutMapping("/{idGasolinera}/trabajadores/{idTrabajador}")
        public ResponseEntity<?> editWorkerWithImage(
                        // @RequestParam("idGasStationWorker") Long idGasStationWorker,
                        @RequestParam("identification") String identification,
                        @RequestParam("name") String name,
                        @RequestParam("phone") String phone,
                        // @RequestParam("imageUrl") String imageUrl,
                        // @RequestParam("idGasStation") Long idGasStation,
                        @RequestParam("image") MultipartFile image, @PathVariable Long idGasolinera,
                        @PathVariable Long idTrabajador, Principal principal) throws IOException {

                System.out.println("Entro al controlador PUTMAPPING");

                System.out.println("Entro al controlador POSTMAPINNG");

                Long authenticatedOwnerId = ownerRepository.getOwnerIdByEmail(principal.getName());

                // Verificar si el usuario es ADMIN o el propietario de la gasolinera
                boolean isAdmin = principal.getName().equals("admin@cligro.tech");
                if (!isAdmin) {
                        // Verificar si el OWNER está intentando acceder a una gasolinera que le
                        // pertenece
                        GasStation gasStation = gasStationRepository.findById(idGasolinera)
                                        .orElse(null);

                        if (gasStation == null || !gasStation.getOwner().getId().equals(authenticatedOwnerId)) {
                                return new ResponseEntity<>(
                                                new GlobalErrorResponse(false,
                                                                "No tienes permiso para editar trabajadores de esta gasolinera"),
                                                HttpStatus.FORBIDDEN);
                        }
                }

                // Convertir el JSON a un objeto GasStationWorkerRequest

                @SuppressWarnings("rawtypes")
                GasStationWorkerRequest gasStationWorker = new GasStationWorkerRequest(
                                identification,
                                name, phone);

                GasStationWorkerResponse worker = gasStationWorkerService.updateGasStationWorkerWithImage(
                                gasStationWorker,
                                image, idGasolinera, idTrabajador);
                GlobalSuccessResponse<?> response;

                response = new GlobalSuccessResponse<>(
                                true,
                                "Trabajador editado correctamente",
                                worker);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

}
