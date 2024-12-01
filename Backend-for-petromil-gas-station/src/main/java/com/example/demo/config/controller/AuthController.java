package com.example.demo.config.controller;


import com.example.demo.domain.dto.AuthCreatedUserRequest;
import com.example.demo.domain.dto.AuthLoginRequest;
import com.example.demo.domain.dto.AuthResponse;
import com.example.demo.domain.dto.GlobalResponse;
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
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest userRequest){


        GlobalResponse<?> response;
        AuthResponse obtainUser = userDetailsService.loginUser(userRequest);

        if(obtainUser == null){

            response = new GlobalResponse<>(
                    false,
                    "Usuario o contraseña incorrectas",
                    "este usuario no existe"
            );

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Respuesta en caso de éxito
        response = new GlobalResponse<>(
                true,
                "Usuario logeado correctamente",
                obtainUser
        );
        return new ResponseEntity<>(response, HttpStatus.OK);




    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody @Valid AuthCreatedUserRequest authCreateUser){


        GlobalResponse<?> response;
        AuthResponse obtainUser = userDetailsService.registerUser(authCreateUser);

        if(obtainUser == null){

            response = new GlobalResponse<>(
                    false,
                    "Este usuario ya existe con este correo electronico",
                    "este usuario ya está registrado"
            );

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Respuesta en caso de éxito
        response = new GlobalResponse<>(
                true,
                "Usaurio registrado correctamente",
                obtainUser
        );
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


}
