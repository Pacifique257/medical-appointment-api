package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.config.JwtUtil;
import com.example.medical_appointment.config.TokenBlacklist;
import com.example.medical_appointment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Changement ici
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // Changement ici
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, TokenBlacklist tokenBlacklist) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder; // Changement ici
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

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