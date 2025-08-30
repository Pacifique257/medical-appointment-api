package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AppointmentService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Créer un rendez-vous", description = "Crée un nouveau rendez-vous pour un patient. Réservé aux PATIENT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        logger.info("Requête POST /api/v1/appointments pour créer un rendez-vous: {}", appointmentDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour la création d'un rendez-vous");
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String patientEmail = authentication.getName();
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO, patientEmail);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @Operation(summary = "Lister tous les rendez-vous", description = "Récupère tous les rendez-vous. Réservé aux ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        logger.info("Requête GET /api/v1/appointments pour lister tous les rendez-vous");
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @Operation(summary = "Récupérer un rendez-vous par ID", description = "Récupère un rendez-vous par son ID. Réservé aux ADMIN, DOCTOR, ou PATIENT concerné.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        logger.info("Requête GET /api/v1/appointments/{} pour récupérer un rendez-vous", id);
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @Operation(summary = "Lister les rendez-vous d'un patient", description = "Récupère les rendez-vous d'un patient authentifié. Réservé aux PATIENT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments() {
        logger.info("Requête GET /api/v1/appointments/patient pour lister les rendez-vous du patient");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour la récupération des rendez-vous du patient");
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String patientEmail = authentication.getName();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientEmail);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @Operation(summary = "Lister les rendez-vous d'un docteur", description = "Récupère les rendez-vous d'un docteur authentifié. Réservé aux DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAppointments() {
        logger.info("Requête GET /api/v1/appointments/doctor pour lister les rendez-vous du docteur");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour la récupération des rendez-vous du docteur");
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String doctorEmail = authentication.getName();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctor(doctorEmail);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @Operation(summary = "Confirmer un rendez-vous", description = "Confirme un rendez-vous. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rendez-vous confirmé avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    })
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentDTO> confirmAppointment(@PathVariable Long id) {
        logger.info("Requête PUT /api/v1/appointments/{}/confirm pour confirmer un rendez-vous", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour la confirmation du rendez-vous ID: {}", id);
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String adminEmail = authentication.getName();
        AppointmentDTO updatedAppointment = appointmentService.confirmAppointment(id, adminEmail);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    @Operation(summary = "Annuler un rendez-vous", description = "Annule un rendez-vous. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rendez-vous annulé avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    })
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        logger.info("Requête PUT /api/v1/appointments/{}/cancel pour annuler un rendez-vous", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour l'annulation du rendez-vous ID: {}", id);
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String adminEmail = authentication.getName();
        AppointmentDTO updatedAppointment = appointmentService.cancelAppointment(id, adminEmail);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    @Operation(summary = "Terminer un rendez-vous", description = "Marque un rendez-vous comme terminé. Réservé aux ADMIN et DOCTOR.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rendez-vous terminé avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    })
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentDTO> completeAppointment(@PathVariable Long id) {
        logger.info("Requête PUT /api/v1/appointments/{}/complete pour terminer un rendez-vous", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.error("Utilisateur non authentifié pour l'achèvement du rendez-vous ID: {}", id);
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        String adminEmail = authentication.getName();
        AppointmentDTO updatedAppointment = appointmentService.completeAppointment(id, adminEmail);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    @Operation(summary = "Lister les docteurs par spécialité", description = "Récupère les docteurs pour une spécialité donnée. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des docteurs récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Spécialité non trouvée")
    })
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctorsBySpecialty(@RequestParam Long specialtyId) {
        logger.info("Requête GET /api/v1/appointments/doctors pour specialtyId: {}", specialtyId);
        List<User> doctors = appointmentService.getDoctorsBySpecialty(specialtyId);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @Operation(summary = "Lister les dates disponibles", description = "Récupère les dates disponibles pour un docteur. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des dates récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/dates")
    public ResponseEntity<List<LocalDate>> getAvailableDates(@RequestParam Long doctorId) {
        logger.info("Requête GET /api/v1/appointments/dates pour doctorId: {}", doctorId);
        List<LocalDate> dates = appointmentService.getAvailableDates(doctorId);
        return new ResponseEntity<>(dates, HttpStatus.OK);
    }

    @Operation(summary = "Lister les créneaux horaires disponibles", description = "Récupère les créneaux horaires disponibles pour un docteur à une date donnée. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des créneaux récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Format de date invalide"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/slots")
    public ResponseEntity<List<String>> getAvailableTimeSlots(@RequestParam Long doctorId, @RequestParam String date) {
        logger.info("Requête GET /api/v1/appointments/slots pour doctorId: {} et date: {}", doctorId, date);
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<String> slots = appointmentService.getAvailableTimeSlots(doctorId, localDate);
            return new ResponseEntity<>(slots, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide: {}", date);
            throw new DateTimeParseException("Format de date invalide. Utilisez yyyy-MM-dd", date, e.getErrorIndex());
        }
    }

    @Operation(summary = "Récupérer les frais de consultation", description = "Récupère les frais de consultation d'un docteur. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Frais de consultation récupérés avec succès"),
        @ApiResponse(responseCode = "404", description = "Docteur non trouvé")
    })
    @GetMapping("/fee")
    public ResponseEntity<Double> getDoctorConsultationFee(@RequestParam Long doctorId) {
        logger.info("Requête GET /api/v1/appointments/fee pour doctorId: {}", doctorId);
        Double fee = appointmentService.getDoctorConsultationFee(doctorId);
        return new ResponseEntity<>(fee, HttpStatus.OK);
    }

    @Operation(summary = "Récupérer une disponibilité spécifique", description = "Récupère une disponibilité pour un docteur, une date et un créneau horaire. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilité récupérée avec succès"),
        @ApiResponse(responseCode = "400", description = "Format de date invalide"),
        @ApiResponse(responseCode = "404", description = "Disponibilité ou docteur non trouvé")
    })
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityDTO> getAvailability(@RequestParam Long doctorId, @RequestParam String date, @RequestParam String timeSlot) {
        logger.info("Requête GET /api/v1/appointments/availability pour doctorId: {}, date: {}, timeSlot: {}", doctorId, date, timeSlot);
        try {
            LocalDate localDate = LocalDate.parse(date);
            AvailabilityDTO availability = appointmentService.getAvailability(doctorId, localDate, timeSlot);
            return new ResponseEntity<>(availability, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide: {}", date);
            throw new DateTimeParseException("Format de date invalide. Utilisez yyyy-MM-dd", date, e.getErrorIndex());
        }
    }
}