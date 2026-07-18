package com.cristinabeatriz.asistencia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RolLoginSuccessHandler rolLoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/acceso-denegado", "/css/**", "/js/**").permitAll()
                // RF-01/RF-02/RF-05/RF-10: solo el docente registra asistencia e incidencias
                .requestMatchers("/asistencia/**").hasRole("DOCENTE")
                // RF-04/RF-09/CU-05: reportes para auxiliar, secretaría y dirección
                .requestMatchers("/reportes/**").hasAnyRole("AUXILIAR", "SECRETARIA", "DIRECCION", "ADMINISTRADOR")
                // RF-03/CU-04: panel en tiempo real para auxiliar y dirección
                .requestMatchers("/panel/**").hasAnyRole("AUXILIAR", "DIRECCION", "ADMINISTRADOR")
                // RF-09/CU-06: historial de asistencia por alumno
                .requestMatchers("/historial/**").hasAnyRole("AUXILIAR", "DIRECCION", "ADMINISTRADOR")
                // RF-07/RF-08/CU-08: gestión de usuarios solo para el administrador
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                // Monitoreo del sistema: solo administrador
                .requestMatchers("/actuator/**").hasRole("ADMINISTRADOR")
                // Dashboard accesible para cualquier usuario autenticado (CU-01)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(rolLoginSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            // El usuario ve una pagina propia de "acceso denegado" en vez de volver al login
            .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));
        return http.build();
    }
}