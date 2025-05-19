package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.config.JwtUtil;
import com.example.medical_appointment.config.TokenBlacklist;
import com.example.medical_appointment.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @Autowired
    public AuthController(UserService userService, BCryptPasswordEncoder passwordEncoder, 
                         JwtUtil jwtUtil, TokenBlacklist tokenBlacklist) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(email);
            response.put("message", "Login successful");
            response.put("accessToken", token);
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklist.blacklistToken(token);
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid token"));
        }
    }
}