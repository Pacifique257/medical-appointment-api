// package com.example.medical_appointment.controller;

// import com.example.medical_appointment.Models.Appointment;
// import com.example.medical_appointment.Models.User;
// import com.example.medical_appointment.dto.DashboardDTO;
// import com.example.medical_appointment.service.AppointmentService;
// import java.time.LocalDate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.List;

// @RestController
// @RequestMapping("/api/dashboard")
// public class DashboardController {

//     private final AppointmentService appointmentService;

//     @Autowired
//     public DashboardController(AppointmentService appointmentService) {
//         this.appointmentService = appointmentService;
//     }

//     @GetMapping
//     public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal User user) {
//         DashboardDTO dashboard = new DashboardDTO();
//         if ("PATIENT".equals(user.getRole())) {
//             List<Appointment> appointments = appointmentService.getPatientAppointments(user);
//             dashboard.setAppointments(appointments);
//         } else if ("DOCTOR".equals(user.getRole())) {
//             List<Appointment> appointments = appointmentService.getDoctorAppointments(user, LocalDate.now());
//             dashboard.setAppointments(appointments);
//         }
//         dashboard.setUser(user);
//         return ResponseEntity.ok(dashboard);
//     }
// }
