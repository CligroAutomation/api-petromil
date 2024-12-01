package com.example.demo.config;

import com.example.demo.Util.JwtUtils;
import com.example.demo.config.filter.JwtTokenValidator;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    // Configurar los endpoints publicos
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    // Configurar los endpoints privados
                    http.requestMatchers(HttpMethod.DELETE, "/superadmin/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/superadmin/**").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/superadmin/**").permitAll();
                    http.requestMatchers(HttpMethod.PUT, "/superadmin/**").permitAll();

                    http.requestMatchers(HttpMethod.POST, "/owner/**").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/owner/**").permitAll();
                    http.requestMatchers(HttpMethod.PUT, "/owner/**").permitAll();
                    http.requestMatchers(HttpMethod.DELETE, "/owner/**").permitAll();

                    http.requestMatchers(HttpMethod.POST, "/gas-station/**").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/gas-station/**").permitAll();
                    http.requestMatchers(HttpMethod.PUT, "/gas-station/**").permitAll();
                    http.requestMatchers(HttpMethod.DELETE, "/gas-station/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/gas-station/create-worker-with-image").permitAll();

                    // Configurar el resto de endpoint - NO ESPECIFICADOS
                    // http.anyRequest().denyAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
