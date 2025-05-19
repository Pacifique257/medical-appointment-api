package com.example.medical_appointment.controller;


import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO,
                                                        @AuthenticationPrincipal User patient) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setReason(appointmentDTO.getReason());
        return ResponseEntity.ok(appointmentService.createAppointment(appointment, appointmentDTO.getAvailabilityId()));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Appointment> confirmAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, user));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Appointment> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @GetMapping("/patient")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@AuthenticationPrincipal User patient) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patient));
    }

    @GetMapping("/doctor/{date}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@AuthenticationPrincipal User doctor,
                                                                  @PathVariable String date) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctor, LocalDate.parse(date)));
    }
}