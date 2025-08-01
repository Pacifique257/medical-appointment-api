package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        logger.info("Requête GET /api/v1/appointments pour lister tous les rendez-vous");
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        logger.info("Requête GET /api/v1/appointments/{} pour récupérer un rendez-vous", id);
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @GetMapping("/patient")
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

    @GetMapping("/doctor")
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

    @PutMapping("/{id}/confirm")
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

    @PutMapping("/{id}/cancel")
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

    @PutMapping("/{id}/complete")
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

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctorsBySpecialty(@RequestParam Long specialtyId) {
        logger.info("Requête GET /api/v1/appointments/doctors pour specialtyId: {}", specialtyId);
        List<User> doctors = appointmentService.getDoctorsBySpecialty(specialtyId);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/dates")
    public ResponseEntity<List<LocalDate>> getAvailableDates(@RequestParam Long doctorId) {
        logger.info("Requête GET /api/v1/appointments/dates pour doctorId: {}", doctorId);
        List<LocalDate> dates = appointmentService.getAvailableDates(doctorId);
        return new ResponseEntity<>(dates, HttpStatus.OK);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<String>> getAvailableTimeSlots(@RequestParam Long doctorId, 
                                                             @RequestParam String date) {
        logger.info("Requête GET /api/v1/appointments/slots pour doctorId: {} et date: {}", doctorId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<String> slots = appointmentService.getAvailableTimeSlots(doctorId, localDate);
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }

    @GetMapping("/fee")
    public ResponseEntity<Double> getDoctorConsultationFee(@RequestParam Long doctorId) {
        logger.info("Requête GET /api/v1/appointments/fee pour doctorId: {}", doctorId);
        Double fee = appointmentService.getDoctorConsultationFee(doctorId);
        return new ResponseEntity<>(fee, HttpStatus.OK);
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityDTO> getAvailability(@RequestParam Long doctorId, 
                                                          @RequestParam String date, 
                                                          @RequestParam String timeSlot) {
        logger.info("Requête GET /api/v1/appointments/availability pour doctorId: {}, date: {}, timeSlot: {}", doctorId, date, timeSlot);
        LocalDate localDate = LocalDate.parse(date);
        AvailabilityDTO availability = appointmentService.getAvailability(doctorId, localDate, timeSlot);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }
}