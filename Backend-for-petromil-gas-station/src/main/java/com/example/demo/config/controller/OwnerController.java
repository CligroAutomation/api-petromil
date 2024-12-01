package com.example.demo.config.controller;


import com.example.demo.dao.OwnerRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.domain.Owner;
import com.example.demo.domain.dto.GlobalResponse;
import com.example.demo.domain.dto.OwnerResponse;
import com.example.demo.service.OwnerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/superadmin")
public class OwnerController {

    @Autowired
    OwnerServiceImpl ownerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OwnerRepository ownerRepository;


    @PostMapping("/create-owner")
    public ResponseEntity<?> postOwner(@RequestBody @Valid OwnerResponse ownerResponse){

        // Llamada al servicio para crear el propietario
        OwnerResponse o = this.ownerService.createOwner(ownerResponse);
        GlobalResponse<?> response;

        // Verificar si la creación del propietario falló (es decir, si el usuario ya tiene el rol ADMIN)
        if(o == null){
            response = new GlobalResponse<>(
                    false,
                    "Este usuario ya tiene el rol ADMIN o ya existe en la base de datos",
                    "El usuario no puede ser creado porque ya existe o tiene el rol ADMIN asignado."
            );

            System.out.println("Validación del controlador");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Si el propietario fue creado correctamente, retornar la respuesta con éxito
        response = new GlobalResponse<>(
                true,
                "Usuario creado correctamente",
                o
        );
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


    @GetMapping("/get-owner/{id}")
    public ResponseEntity<?>  getOwner(@PathVariable Long id){

        System.out.println("Entro al controlador");

        OwnerResponse o = this.ownerService.getOwner(id);
        GlobalResponse<?> response;

        if(o == null){

            response = new GlobalResponse<>(
                    false,
                    "No se pudo encontrar el usuario, al parecer no existe",
                    "No se pudo encontrar la id"
            );

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }

        // Respuesta en caso de éxito
        response = new GlobalResponse<>(
                true,
                "Dueño obtenido correctamente",
                o
        );
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


    @PutMapping("/update-owner")
    public ResponseEntity<?>  updateOwner(@RequestBody OwnerResponse ownerResponse){

        System.out.println("ENTRO AL PUTMMAPING");

        OwnerResponse o = this.ownerService.updateOwner(ownerResponse);
        GlobalResponse<?> response;

        if (o == null) {
            response = new GlobalResponse<>(
                    false,
                    "No se pudo encontrar el dueño, al parecer no existe",
                    "No se pudo encontrar la id"
            );

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);


        }

        // Respuesta en caso de éxito
        response = new GlobalResponse<>(
                true,
                "Dueño actualizado correctamente",
                o
        );
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    @DeleteMapping("/delete-owner/{id}")
    public ResponseEntity<?>  deleteOwner(@PathVariable Long id){

        System.out.println("ENTRO AL DELETEmAPPING");
        GlobalResponse<?> response;

        OwnerResponse owner = this.ownerService.deleteOwner(id);

        if(owner == null){
            response = new GlobalResponse<>(
                    false,
                    "No se pudo encontrar el dueño, al parecer no existe",
                    "No se pudo encontrar la id"
            );

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }

        // Respuesta en caso de éxito
        response = new GlobalResponse<>(
                true,
                "Dueño eliminado correctamente",
                owner
        );
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


    @GetMapping("/get-all-owners")
    public ResponseEntity<?> getAllOwnerByUserState() {

        // Asegúrate de que getAllOwnerByUserState nunca devuelva null
        List<Owner> owners = ownerService.getAllOwnerByUserState();
        GlobalResponse<?> response;

        if (owners == null || owners.isEmpty()) {
            response = new GlobalResponse<>(
                    false,
                    "No hay owners",
                    "No hay owners en la base de datos"
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Transformar los objetos Owner a OwnerResponse
        List<OwnerResponse> ownersResponse = owners.stream()
                .map(o -> new OwnerResponse(
                        o.getId(),
                        o.getUser().getIdentification(),
                        o.getName(),
                        o.getPhone(),
                        o.getUser().getEmail(),
                        o.getUser().getPassword()
                ))
                .collect(Collectors.toList());

        response = new GlobalResponse<>(
                true,
                "Dueños obtenidos correctamente",
                ownersResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
