package com.example.medical_appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserDetailsService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Authentification et inscription accessibles à tous
                .requestMatchers("/api/v1/auth/**",
                                "/api/v1/users/register",
                                "/api/v1/users/admin/register").permitAll()
                // Accès spécialités
                .requestMatchers("/api/v1/specialties/**").hasAnyRole("ADMIN", "DOCTOR")
                // Disponibilités : lecture publique, écriture pour ADMIN et DOCTOR
                .requestMatchers(HttpMethod.GET, "/api/v1/availabilities/**").permitAll()
                .requestMatchers("/api/v1/availabilities/**").hasAnyRole("ADMIN", "DOCTOR")
                // Gestion des utilisateurs
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                // Gestion des rendez-vous
                .requestMatchers("/api/v1/appointments/**").hasAnyRole("DOCTOR", "PATIENT", "ADMIN")
                // Documentation Swagger
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                // Tout autre endpoint nécessite une authentification
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}