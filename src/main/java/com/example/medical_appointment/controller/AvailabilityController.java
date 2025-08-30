package com.example.medical_appointment.controller;

import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/availabilities")
public class AvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);
    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Operation(summary = "Créer une disponibilité", description = "Crée une nouvelle disponibilité pour un docteur. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Disponibilité créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AvailabilityDTO> createAvailability(@Valid @RequestBody AvailabilityDTO availabilityDTO) {
        logger.info("Requête POST /api/v1/availabilities pour créer une disponibilité: {}", availabilityDTO);
        AvailabilityDTO createdAvailability = availabilityService.createAvailability(availabilityDTO);
        return new ResponseEntity<>(createdAvailability, HttpStatus.CREATED);
    }

    @Operation(summary = "Lister toutes les disponibilités", description = "Récupère toutes les disponibilités. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des disponibilités récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<List<AvailabilityDTO>> getAllAvailabilities() {
        logger.info("Requête GET /api/v1/availabilities pour lister toutes les disponibilités");
        List<AvailabilityDTO> availabilities = availabilityService.getAllAvailabilities();
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @Operation(summary = "Récupérer une disponibilité par ID", description = "Récupère une disponibilité par son ID. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilité trouvée"),
        @ApiResponse(responseCode = "404", description = "Disponibilité non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDTO> getAvailabilityById(@PathVariable Long id) {
        logger.info("Requête GET /api/v1/availabilities/{} pour récupérer une disponibilité", id);
        AvailabilityDTO availability = availabilityService.getAvailabilityById(id);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    @Operation(summary = "Lister les disponibilités par docteur", description = "Récupère les disponibilités d'un docteur. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des disponibilités récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilitiesByDoctor(@PathVariable Long doctorId) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{} pour lister les disponibilités du docteur", doctorId);
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByDoctor(doctorId);
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    @Operation(summary = "Lister les disponibilités par docteur et date", description = "Récupère les disponibilités d'un docteur pour une date donnée. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des disponibilités récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Format de date invalide"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<?> getAvailabilitiesByDoctorAndDate(@PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/date/{} pour lister les disponibilités", doctorId, date);
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByDoctorAndDate(doctorId, localDate);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide: {}", date);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Format de date invalide. Utilisez yyyy-MM-dd"));
        }
    }

    @Operation(summary = "Lister les créneaux libres par docteur et date", description = "Récupère les créneaux libres d'un docteur pour une date donnée. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des créneaux libres récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Format de date invalide"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/doctor/{doctorId}/available/date/{date}")
    public ResponseEntity<?> getAvailableSlotsByDoctorAndDate(@PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/available/date/{} pour lister les créneaux libres", doctorId, date);
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlotsByDoctorAndDate(doctorId, localDate);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide: {}", date);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Format de date invalide. Utilisez yyyy-MM-dd"));
        }
    }

    @Operation(summary = "Lister les créneaux libres après une date", description = "Récupère les créneaux libres d'un docteur après une date donnée. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des créneaux libres récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Format de date invalide"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/doctor/{doctorId}/available/after/{date}")
    public ResponseEntity<?> getAvailableSlotsByDoctorAfterDate(@PathVariable Long doctorId, @PathVariable String date) {
        logger.info("Requête GET /api/v1/availabilities/doctor/{}/available/after/{} pour lister les créneaux libres", doctorId, date);
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlotsByDoctorAfterDate(doctorId, localDate);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide: {}", date);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Format de date invalide. Utilisez yyyy-MM-dd"));
        }
    }

    @Operation(summary = "Mettre à jour une disponibilité", description = "Met à jour une disponibilité existante. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Disponibilité non trouvée")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AvailabilityDTO> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityDTO availabilityDTO) {
        logger.info("Requête PUT /api/v1/availabilities/{} pour mettre à jour une disponibilité", id);
        availabilityDTO.setId(id);
        AvailabilityDTO updatedAvailability = availabilityService.updateAvailability(id, availabilityDTO);
        return new ResponseEntity<>(updatedAvailability, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer une disponibilité", description = "Supprime une disponibilité par son ID. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Disponibilité supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Disponibilité non trouvée")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        logger.info("Requête DELETE /api/v1/availabilities/{} pour supprimer une disponibilité", id);
        availabilityService.deleteAvailability(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
