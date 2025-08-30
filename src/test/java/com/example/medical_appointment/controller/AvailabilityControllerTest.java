package com.example.medical_appointment.controller;

import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AvailabilityControllerTest {

    @InjectMocks
    private AvailabilityController availabilityController;

    @Mock
    private AvailabilityService availabilityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAvailabilitySuccess() {
        // Arrange
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        when(availabilityService.createAvailability(availabilityDTO)).thenReturn(availabilityDTO);

        // Act
        ResponseEntity<AvailabilityDTO> response = availabilityController.createAvailability(availabilityDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(availabilityDTO, response.getBody());
    }

    @Test
    public void testGetAllAvailabilitiesSuccess() {
        // Arrange
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        when(availabilityService.getAllAvailabilities()).thenReturn(List.of(availabilityDTO));

        // Act
        ResponseEntity<List<AvailabilityDTO>> response = availabilityController.getAllAvailabilities();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAvailabilityByIdSuccess() {
        // Arrange
        Long id = 1L;
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        when(availabilityService.getAvailabilityById(id)).thenReturn(availabilityDTO);

        // Act
        ResponseEntity<AvailabilityDTO> response = availabilityController.getAvailabilityById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availabilityDTO, response.getBody());
    }

    @Test
    public void testGetAvailabilitiesByDoctorAndDateSuccess() {
        // Arrange
        Long doctorId = 1L;
        String date = "2025-08-01";
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        when(availabilityService.getAvailabilitiesByDoctorAndDate(doctorId, LocalDate.parse(date))).thenReturn(List.of(availabilityDTO));

        // Act
        ResponseEntity<?> response = availabilityController.getAvailabilitiesByDoctorAndDate(doctorId, date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    public void testGetAvailabilitiesByDoctorAndDateInvalidDate() {
        // Arrange
        Long doctorId = 1L;
        String date = "invalid-date";

        // Act
        ResponseEntity<?> response = availabilityController.getAvailabilitiesByDoctorAndDate(doctorId, date);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Format de date invalide. Utilisez yyyy-MM-dd", ((Map<?, ?>) response.getBody()).get("error"));
    }
}