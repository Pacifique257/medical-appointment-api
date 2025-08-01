package com.example.medical_appointment.controller;

import com.example.medical_appointment.dto.SpecialtyDTO;
import com.example.medical_appointment.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @Autowired
    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @Operation(summary = "Créer une nouvelle spécialité", description = "Crée une nouvelle spécialité. Réservé aux utilisateurs avec le rôle ADMIN. Nécessite une authentification JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Spécialité créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé (rôle ADMIN requis)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyDTO> createSpecialty(@Valid @RequestBody SpecialtyDTO specialtyDTO) {
        SpecialtyDTO createdSpecialty = specialtyService.createSpecialty(specialtyDTO);
        return new ResponseEntity<>(createdSpecialty, HttpStatus.CREATED);
    }

    @Operation(summary = "Lister toutes les spécialités", description = "Récupère la liste de toutes les spécialités. Accessible aux rôles ADMIN et DOCTOR. Nécessite une authentification JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des spécialités récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé (rôle ADMIN ou DOCTOR requis)")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<SpecialtyDTO>> getAllSpecialties() {
        List<SpecialtyDTO> specialties = specialtyService.getAllSpecialties();
        return new ResponseEntity<>(specialties, HttpStatus.OK);
    }

    @Operation(summary = "Récupérer une spécialité par ID", description = "Récupère une spécialité spécifique par son ID. Accessible aux rôles ADMIN et DOCTOR. Nécessite une authentification JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Spécialité trouvée"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé (rôle ADMIN ou DOCTOR requis)"),
        @ApiResponse(responseCode = "404", description = "Spécialité non trouvée")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<SpecialtyDTO> getSpecialtyById(@PathVariable Long id) {
        SpecialtyDTO specialty = specialtyService.getSpecialtyById(id);
        return new ResponseEntity<>(specialty, HttpStatus.OK);
    }

    @Operation(summary = "Mettre à jour une spécialité", description = "Met à jour une spécialité existante. Réservé aux utilisateurs avec le rôle ADMIN. Nécessite une authentification JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Spécialité mise à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé (rôle ADMIN requis)"),
        @ApiResponse(responseCode = "404", description = "Spécialité non trouvée")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyDTO> updateSpecialty(@PathVariable Long id, @Valid @RequestBody SpecialtyDTO specialtyDTO) {
        SpecialtyDTO updatedSpecialty = specialtyService.updateSpecialty(id, specialtyDTO);
        return new ResponseEntity<>(updatedSpecialty, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer une spécialité", description = "Supprime une spécialité par son ID. Réservé aux utilisateurs avec le rôle ADMIN. Nécessite une authentification JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Spécialité supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Authentification requise"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé (rôle ADMIN requis)"),
        @ApiResponse(responseCode = "404", description = "Spécialité non trouvée")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}