package com.example.medical_appointment.controller;



import com.example.medical_appointment.dto.SpecialtyDTO;
import com.example.medical_appointment.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SpecialtyControllerTest {

    @InjectMocks
    private SpecialtyController specialtyController;

    @Mock
    private SpecialtyService specialtyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateSpecialtySuccess() {
        // Arrange
        SpecialtyDTO specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setName("Cardiologie");
        when(specialtyService.createSpecialty(specialtyDTO)).thenReturn(specialtyDTO);

        // Act
        ResponseEntity<SpecialtyDTO> response = specialtyController.createSpecialty(specialtyDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(specialtyDTO, response.getBody());
    }

    @Test
    public void testGetAllSpecialtiesSuccess() {
        // Arrange
        SpecialtyDTO specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setName("Cardiologie");
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(specialtyDTO));

        // Act
        ResponseEntity<List<SpecialtyDTO>> response = specialtyController.getAllSpecialties();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Cardiologie", response.getBody().get(0).getName());
    }

    @Test
    public void testGetSpecialtyByIdSuccess() {
        // Arrange
        Long id = 1L;
        SpecialtyDTO specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setId(id);
        specialtyDTO.setName("Cardiologie");
        when(specialtyService.getSpecialtyById(id)).thenReturn(specialtyDTO);

        // Act
        ResponseEntity<SpecialtyDTO> response = specialtyController.getSpecialtyById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(specialtyDTO, response.getBody());
    }

    @Test
    public void testUpdateSpecialtySuccess() {
        // Arrange
        Long id = 1L;
        SpecialtyDTO specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setName("Cardiologie");
        when(specialtyService.updateSpecialty(id, specialtyDTO)).thenReturn(specialtyDTO);

        // Act
        ResponseEntity<SpecialtyDTO> response = specialtyController.updateSpecialty(id, specialtyDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(specialtyDTO, response.getBody());
    }

    @Test
    public void testDeleteSpecialtySuccess() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<Void> response = specialtyController.deleteSpecialty(id);

        // Assert
        verify(specialtyService).deleteSpecialty(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
