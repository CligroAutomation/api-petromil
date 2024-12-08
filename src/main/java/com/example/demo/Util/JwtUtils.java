package com.example.demo.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//1 1 en JWT - Aquí es donde creamos el token JWT y hacemos otros métodos
@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {

        // Definimos el algoritmo de encriptación

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        // Obtenemos el usuario
        String username = authentication.getPrincipal().toString();

        // Obtenemos los permisos separados por coma

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(userGenerator) // Creador issuer del token el cual es una palabra "secreta"
                .withSubject(username) // Sujeto del token, el cual es el username
                .withClaim("authorities", authorities) // Obtenemos los roles o permisos del usuario
                .withIssuedAt(new Date()) // fecha y hora en la qué se emitió el token
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // Fecha y hora de expiración del token.
                                                                                // 30 min despues
                .withJWTId(UUID.randomUUID().toString()) // Identificador único al token
                .withNotBefore(new Date(System.currentTimeMillis())) // Define la fecha y hora antes de la cual el token
                                                                     // no es válido.
                .sign(algorithm); // Firma el token utilizando el algoritmo especificado en la variable algorithm.

        return jwtToken;

    }

    public DecodedJWT validateToken(String token) {
        try {

            // Crea una instancia de Algorithm usando el algoritmo HMAC256. con la clave
            // secreta
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm) // Inicia la configuración de un verificador de token JWT
                                                          // utilizando el algoritmo especificado
                    .withIssuer(this.userGenerator) // Especifica el emisor esperado del token
                    .build(); // Construye el verificador de token (JWTVerifier) con las configuraciones
                              // proporcionada

            // Verifica el token proporcionado contra las reglas del verificador.
            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT;

        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token invalid, not authorized");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject().toString();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT
                .getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }

}
