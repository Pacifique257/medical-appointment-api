package com.example.medical_appointment.controller;

import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AvailabilityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availabilities")
public class AvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityDTO> createAvailability(@Valid @RequestBody AvailabilityDTO availabilityDTO) {
        logger.info("Requête POST /api/v1/availabilities pour créer une disponibilité: {}", availabilityDTO);
        AvailabilityDTO createdAvailability = availabilityService.createAvailability(availabilityDTO);
        return new ResponseEntity<>(createdAvailability, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityDTO>> getAllAvailabilities() {
        logger.info("Requête GET /api/v1/availabilities pour lister toutes les disponibilités");
        List<AvailabilityDTO> availabilities = availabilityService.getAllAvailabilities();
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDTO> getAvailabilityById(@PathVariable Long id) {
        logger.info("Requête GET /api/v1/availabilities/{} pour récupérer une disponibilité", id);
        AvailabilityDTO availability = availabilityService.getAvailabilityById(id);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilitiesByDoctor(@PathVariable Long doctorId) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{} pour lister les disponibilités du docteur", doctorId);
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByDoctor(doctorId);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilitiesByDoctorAndDate(
            @PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/date/{} pour lister les disponibilités", doctorId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByDoctorAndDate(doctorId, localDate);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/available/date/{date}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailableSlotsByDoctorAndDate(
            @PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/available/date/{} pour lister les créneaux libres", doctorId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlotsByDoctorAndDate(doctorId, localDate);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/available/after/{date}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailableSlotsByDoctorAfterDate(
            @PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/available/after/{} pour lister les créneaux libres", doctorId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlotsByDoctorAfterDate(doctorId, localDate);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDTO> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityDTO availabilityDTO) {
        logger.info("Requête PUT /api/v1/availabilities/{} pour mettre à jour une disponibilité", id);
        availabilityDTO.setId(id);
        AvailabilityDTO updatedAvailability = availabilityService.updateAvailability(id, availabilityDTO);
        return new ResponseEntity<>(updatedAvailability, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        logger.info("Requête DELETE /api/v1/availabilities/{} pour supprimer une disponibilité", id);
        availabilityService.deleteAvailability(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}