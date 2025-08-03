package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.config.JwtUtil;
import com.example.medical_appointment.config.TokenBlacklist;
import com.example.medical_appointment.dto.LoginDTO;
import com.example.medical_appointment.service.UserService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final Bucket rateLimitBucket;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, TokenBlacklist tokenBlacklist, Bucket rateLimitBucket) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.rateLimitBucket = rateLimitBucket;
    }

    @Operation(summary = "Authentifier un utilisateur", description = "Génère un token JWT pour un utilisateur avec des credentials valides.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Mot de passe invalide"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO credentials) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la connexion");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }
        String email = credentials.getEmail();
        String password = credentials.getPassword();
        User user = userService.getUserByEmail(email);
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            logger.warn("Login failed: User not found for email: {}", email);
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(email, user.getRole());
            response.put("message", "Login successful");
            response.put("accessToken", token);
            response.put("userId", user.getId());
            response.put("role", user.getRole());
            logger.info("Login successful, userId: {}, role: {}", user.getId(), user.getRole());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Login failed: Invalid password for email: {}", email);
            response.put("error", "Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Operation(summary = "Déconnexion d'un utilisateur", description = "Ajoute le token JWT à la liste noire pour déconnexion.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "400", description = "Token invalide ou manquant")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklist.blacklistToken(token);
            logger.info("Logout successful, token blacklisted");
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } else {
            logger.warn("Logout failed: Invalid or missing token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid token"));
        }
    }
}