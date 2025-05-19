package com.example.medical_appointment.service;


import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLastName("Doe");
        user.setFirstName("John");
        user.setEmail("john.doe@example.com");
        user.setRole("PATIENT");
        user.setPhone("+1234567890");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("password");
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("john.doe@example.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserEmailExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });
        assertEquals("Email already exists", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmailFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);

        User foundUser = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(foundUser);
        assertEquals("john.doe@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        User foundUser = userService.getUserByEmail("unknown@example.com");

        assertNull(foundUser);
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}