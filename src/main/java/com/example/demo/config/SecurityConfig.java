package com.example.demo.config;

import com.example.demo.Util.JwtUtils;
import com.example.demo.config.controller.CustomAccessDeniedHandler;
import com.example.demo.config.controller.CustomAuthenticationEntryPoint;
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

        private final CustomAccessDeniedHandler accessDeniedHandler;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;

        public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
                        CustomAuthenticationEntryPoint authenticationEntryPoint) {
                this.accessDeniedHandler = accessDeniedHandler;
                this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .httpBasic(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exception -> {
                                        // Configura los manejadores personalizados
                                        exception.accessDeniedHandler(accessDeniedHandler);
                                        exception.authenticationEntryPoint(authenticationEntryPoint);
                                })
                                .authorizeHttpRequests(http -> {
                                        // Configurar los endpoints publicos
                                        http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                                        http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();

                                        // Configurar los endpoints privados

                                        // Ownercontroller
                                        http.requestMatchers(HttpMethod.POST, "/propietarios").hasRole("ADMIN"); // Check
                                                                                                                 // postOwner
                                        http.requestMatchers(HttpMethod.GET, "/propietarios/{idPropietario}")
                                                        .hasRole("ADMIN"); // Check
                                                                           // getOwner
                                        http.requestMatchers(HttpMethod.PUT, "/propietarios/{idPropietario}")
                                                        .hasRole("ADMIN"); // Check
                                                                           // putOwner
                                        http.requestMatchers(HttpMethod.DELETE, "/propietarios/{idPropietario}")
                                                        .hasRole("ADMIN"); // Check
                                                                           // delete
                                                                           // Owner
                                        http.requestMatchers(HttpMethod.GET, "/propietarios").hasRole("ADMIN"); // check
                                                                                                                // getAllOwnerByUserState

                                        // GasStationController
                                        http.requestMatchers(HttpMethod.POST, "/propietarios/{idOwner}/gasolineras")
                                                        .hasRole("ADMIN"); // Check
                                                                           // postGasStation
                                        http.requestMatchers(HttpMethod.GET, "/propietarios/{idOwner}/gasolineras")
                                                        .hasAnyRole("ADMIN",
                                                                        "OWNER"); // Check getGasStationsByIdOwner
                                        http.requestMatchers(HttpMethod.GET, "/propietarios/gasolineras")
                                                        .hasRole("ADMIN"); // Check
                                                                           // getAllGasStation
                                        http.requestMatchers(HttpMethod.PUT,
                                                        "/propietarios/{idPropietario}/gasolineras/{idGasolinera}")
                                                        .hasAnyRole("ADMIN", "OWNER"); // Check editGasStation
                                        http.requestMatchers(HttpMethod.DELETE,
                                                        "/propietarios/{idPropietario}/gasolineras/{idGasolinera}")
                                                        .hasRole("ADMIN"); // Check deleteGasStation

                                        // WorkerController
                                        http.requestMatchers(HttpMethod.GET, "/gasolineras/{idGasolinera}/trabajadores")
                                                        .permitAll(); // getWorkersByIdGasStation
                                                                      // ---
                                        http.requestMatchers(HttpMethod.GET,
                                                        "/gasolineras/trabajadores/{workerIdentification}")
                                                        .hasAnyRole("ADMIN", "OWNER"); // getWorkersByIdentification --
                                                                                       // Revisar
                                        http.requestMatchers(HttpMethod.DELETE,
                                                        "/gasolineras/{idGasolinera}/trabajadores/{idTrabajador}")
                                                        .hasAnyRole("ADMIN", "OWNER"); // Check deleteWorkerById
                                        http.requestMatchers(HttpMethod.POST,
                                                        "/gasolineras/{idGasolinera}/trabajadores")
                                                        .hasAnyRole("ADMIN", "OWNER"); // Check addWorkerWithImage
                                        http.requestMatchers(HttpMethod.PUT,
                                                        "/gasolineras/{idGasolinera}/trabajadores/{idTrabajador}")
                                                        .hasAnyRole("ADMIN", "OWNER"); // check editWorkerWithImage

                                        // Surveys
                                        http.requestMatchers(HttpMethod.POST, "/encuestas").permitAll();
                                        http.requestMatchers(HttpMethod.GET, "/encuestas/trabajadores/{idTrabajador}")
                                                        .hasAnyRole("ADMIN",
                                                                        "OWNER");
                                        http.requestMatchers(HttpMethod.GET, "/encuestas/gasolineras/{idGasStation}")
                                                        .hasAnyRole("ADMIN",
                                                                        "OWNER");

                                        http.requestMatchers(HttpMethod.GET, "/health").permitAll();

                                        http.anyRequest().denyAll();
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
