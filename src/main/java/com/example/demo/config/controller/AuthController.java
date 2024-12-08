package com.example.demo.config.controller;

import com.example.demo.domain.dto.*;
import com.example.demo.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest userRequest) {

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse responseError;

            AuthResponse obtainUser = userDetailsService.loginUser(userRequest);

            if (obtainUser == null) {

                responseError = new GlobalErrorResponse(
                        false,
                        "Usuario o contraseña incorrectas");

                return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
            }

            // Respuesta en caso de éxito
            response = new GlobalSuccessResponse<>(
                    true,
                    "Usuario logeado correctamente",
                    obtainUser);
            return new ResponseEntity<>(response, HttpStatus.OK);




    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody @Valid AuthCreatedUserRequest authCreateUser) {

        GlobalSuccessResponse<?> response;
        GlobalErrorResponse responseError;
        AuthResponse obtainUser = userDetailsService.registerUser(authCreateUser);

        if (obtainUser == null) {

            responseError = new GlobalErrorResponse(
                    false,
                    "Este usuario ya existe con este correo electronico || contraseña muy corta || roles no existen");

            return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
        }

        // Respuesta en caso de éxito
        response = new GlobalSuccessResponse<>(
                true,
                "Usaurio registrado correctamente",
                obtainUser);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
