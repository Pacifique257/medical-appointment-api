package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.UserRepository;
import com.example.medical_appointment.dto.UserDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInitialAdminUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Super");
        userDTO.setLastName("Admin");
        userDTO.setEmail("admin@example.com");
        userDTO.setRole("ADMIN");
        userDTO.setPhone("+1234567890");
        userDTO.setBirthDate("1980-01-01");
        userDTO.setPassword("admin123");

        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> userService.createInitialAdminUser(userDTO));

        User savedUser = new User();
        savedUser.setEmail("admin@example.com");
        savedUser.setRole("ADMIN");
        assertEquals("ADMIN", savedUser.getRole());
    }

    @Test
    void createInitialAdminUser_AlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("admin@example.com");

        User existingAdmin = new User();
        existingAdmin.setRole("ADMIN");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(existingAdmin));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createInitialAdminUser(userDTO));
        assertEquals("Un compte admin existe déjà. Utilisez l'endpoint /api/v1/users pour créer d'autres admins.", exception.getMessage());
    }
}