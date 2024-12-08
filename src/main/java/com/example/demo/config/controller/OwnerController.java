package com.example.demo.config.controller;

import com.example.demo.dao.OwnerRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.domain.Owner;
import com.example.demo.domain.dto.GlobalErrorResponse;
import com.example.demo.domain.dto.GlobalSuccessResponse;
import com.example.demo.domain.dto.OwnerResponse;
import com.example.demo.service.OwnerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/propietarios")
public class OwnerController {

        @Autowired
        OwnerServiceImpl ownerService;

        @Autowired
        UserRepository userRepository;

        @Autowired
        OwnerRepository ownerRepository;

        @PostMapping
        public ResponseEntity<?> postOwner(@RequestBody @Valid OwnerResponse ownerResponse) {

                System.out.println("controlador postOwner");
                // Llamada al servicio para crear el propietario
                OwnerResponse o = this.ownerService.createOwner(ownerResponse);
                GlobalSuccessResponse<?> response;
                GlobalErrorResponse responseError;

                // Verificar si la creación del propietario falló (es decir, si el usuario ya
                // tiene el rol ADMIN)
                if (o == null) {
                        responseError = new GlobalErrorResponse(false, "Este usuario ya tiene el rol ADMIN o ya existe en la base de datos");
                        System.out.println("Validación del controlador");
                        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }

                // Si el propietario fue creado correctamente, retornar la respuesta con éxito
                response = new GlobalSuccessResponse<>(
                                true,
                                "Usuario creado correctamente",
                                o);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @GetMapping("/{idPropietario}")
        public ResponseEntity<?> getOwner(@PathVariable Long idPropietario) {
                System.out.println("Entro al controlador GET BY ID OWNER");
                GlobalSuccessResponse<?> response;
                GlobalErrorResponse responseError;
                OwnerResponse o = this.ownerService.getOwner(idPropietario);

                if (o == null) {

                        responseError = new GlobalErrorResponse(false, "No se pudo encontrar el usuario, al parecer no existe");

                        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);

                }

                        // Respuesta en caso de éxito
                response = new GlobalSuccessResponse<>(
                                true,
                                "Dueño obtenido correctamente",
                                o);
                return new ResponseEntity<>(response, HttpStatus.OK);




        }

        @PutMapping("/{idPropietario}")
        public ResponseEntity<?> updateOwner(@PathVariable Long idPropietario, @RequestBody OwnerResponse ownerResponse) {

                System.out.println("ENTRO AL PUTMMAPING");


                OwnerResponse o = this.ownerService.updateOwner(idPropietario,ownerResponse);
                GlobalSuccessResponse<?> response;
                GlobalErrorResponse responseError;


                if (o == null) {
                        responseError = new GlobalErrorResponse(
                                        false,
                                        "No se pudo encontrar el dueño, al parecer no existe");

                        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);

                }

                // Respuesta en caso de éxito
                response = new GlobalSuccessResponse<>(
                                true,
                                "Dueño actualizado correctamente",
                                o);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @DeleteMapping("/{idPropietario}")
        public ResponseEntity<?> deleteOwner(@PathVariable Long idPropietario) {

                System.out.println("ENTRO AL DELETEmAPPING");
                GlobalSuccessResponse<?> response;

                GlobalErrorResponse responseError;

                OwnerResponse owner = this.ownerService.deleteOwner(idPropietario);

                if (owner == null) {
                        responseError = new GlobalErrorResponse(false, "No se pudo encontrar el dueño, al parecer no existe");

                        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);

                }

                // Respuesta en caso de éxito
                response = new GlobalSuccessResponse<>(
                                true,
                                "Dueño eliminado correctamente",
                                owner);
                return new ResponseEntity<>(response, HttpStatus.OK);

        }

        @GetMapping
        public ResponseEntity<?> getAllOwnerByUserState() {

                // Asegúrate de que getAllOwnerByUserState nunca devuelva null
                List<Owner> owners = ownerService.getAllOwnerByUserState();
                GlobalSuccessResponse<?> response;
                GlobalErrorResponse responseError;


                if (owners == null || owners.isEmpty()) {
                        responseError = new GlobalErrorResponse(
                                        false,
                                        "No hay owners");
                        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
                }

                // Transformar los objetos Owner a OwnerResponse
                List<OwnerResponse> ownersResponse = owners.stream()
                                .map(o -> new OwnerResponse(
                                                o.getId(),
                                                o.getUser().getIdentification(),
                                                o.getName(),
                                                o.getPhone(),
                                                o.getUser().getEmail(),
                                                o.getUser().getPassword()))
                                .collect(Collectors.toList());

                response = new GlobalSuccessResponse<>(
                                true,
                                "Dueños obtenidos correctamente",
                                ownersResponse);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

}
