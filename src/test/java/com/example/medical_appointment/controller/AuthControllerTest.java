package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.config.JwtUtil;
import com.example.medical_appointment.dto.LoginDTO;
import com.example.medical_appointment.config.TokenBlacklist;
import com.example.medical_appointment.service.UserService;
import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;



public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private Bucket rateLimitBucket;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("PATIENT");

        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "PATIENT")).thenReturn("jwt-token");

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("accessToken"));
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals(1L, response.getBody().get("userId"));
        assertEquals("PATIENT", response.getBody().get("role"));
    }

    @Test
    public void testLoginUserNotFound() {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password");

        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().get("error"));
    }

    @Test
    public void testLoginInvalidPassword() {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("wrongPassword");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid password", response.getBody().get("error"));
    }

    @Test
    public void testLoginRateLimitExceeded() {
        // Arrange
        LoginDTO credentials = new LoginDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password");

        when(rateLimitBucket.tryConsume(1)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("Limite de débit dépassée", response.getBody().get("error"));
    }

    @Test
    public void testLogoutSuccess() {
        // Arrange
        String token = "jwt-token";
        String authorizationHeader = "Bearer " + token;

        // Act
        ResponseEntity<Map<String, String>> response = authController.logout(authorizationHeader);

        // Assert
        verify(tokenBlacklist).blacklistToken(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody().get("message"));
    }

    @Test
    public void testLogoutInvalidToken() {
        // Arrange
        String authorizationHeader = "InvalidHeader";

        // Act
        ResponseEntity<Map<String, String>> response = authController.logout(authorizationHeader);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token", response.getBody().get("error"));
    }
}   

