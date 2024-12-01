package com.example.demo.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.Util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

//2 en jwt (Aquí validamos el token y guardamos los usuarios autenticados en el contextHolder
public class JwtTokenValidator extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;

    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Obtiene le token
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        // Validamos
        if (jwtToken != null) {

            // Obtenemos el token correctamente eliminando la palabra bearer
            jwtToken = jwtToken.substring(7);
            // Validamos el token utilizando jwtUtils.validateToken y lo guardamos en un
            // decoded
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            // Obtenemos el username utilizando jwtUtils
            String email = jwtUtils.extractUsername(decodedJWT);
            // Obtenemos los permisos
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
            // Convertimos esos permisos en una lista
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(stringAuthorities);
            // Guardamos el username y los permisos en el security context holder
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        }

        filterChain.doFilter(request, response);

    }

}
