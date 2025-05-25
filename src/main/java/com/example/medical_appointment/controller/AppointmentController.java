package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.service.AppointmentService;
import com.example.medical_appointment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

@GetMapping("/create")
    public ModelAndView showAppointmentForm(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/appointments/create - Starting request processing, token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/appointments/create - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User patient = userService.getUserByEmail(userDetails.getUsername());
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            System.out.println("GET /api/appointments/create - Invalid patient: " + userDetails.getUsername());
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("appointment-form");
        modelAndView.addObject("appointmentDTO", new AppointmentDTO());
        modelAndView.addObject("specialties", appointmentService.getAllSpecialties());
        modelAndView.addObject("token", token);
        modelAndView.addObject("patientId", patient.getId());
        System.out.println("GET /api/appointments/create - Rendering form for patient ID: " + patient.getId());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createAppointment(@ModelAttribute AppointmentDTO appointmentDTO, 
                                         @RequestParam(value = "token", required = false) String token) {
        System.out.println("POST /api/appointments/create - Starting request processing, token: " + token);
        System.out.println("POST /api/appointments/create - AppointmentDTO: availabilityId=" + appointmentDTO.getAvailabilityId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("POST /api/appointments/create - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            appointmentService.createAppointment(appointmentDTO, userDetails.getUsername());
            System.out.println("POST /api/appointments/create - Appointment created successfully, redirecting to patient appointments");
            return new ModelAndView("redirect:/api/appointments/patient?token=" + token);
        } catch (IllegalArgumentException e) {
            System.out.println("POST /api/appointments/create - Error: " + e.getMessage());
            ModelAndView modelAndView = new ModelAndView("appointment-form");
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("appointmentDTO", appointmentDTO);
            modelAndView.addObject("specialties", appointmentService.getAllSpecialties());
            modelAndView.addObject("token", token);
            return modelAndView;
        }
    }

    @GetMapping("/patient")
    public ModelAndView showPatientAppointments(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/appointments/patient - Starting request processing");
        System.out.println("GET /api/appointments/patient - Received token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/appointments/patient - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User patient = userService.getUserByEmail(userDetails.getUsername());
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            System.out.println("GET /api/appointments/patient - Invalid patient");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("patient-appointments");
        modelAndView.addObject("appointments", appointmentService.getPatientAppointments(patient.getId()));
        modelAndView.addObject("token", token);
        return modelAndView;
    }

    @GetMapping("/doctor")
    public ModelAndView showDoctorAppointments(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/appointments/doctor - Starting request processing");
        System.out.println("GET /api/appointments/doctor - Received token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/appointments/doctor - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("GET /api/appointments/doctor - Invalid doctor");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("doctor-appointments");
        modelAndView.addObject("appointments", appointmentService.getDoctorAppointments(doctor.getId()));
        modelAndView.addObject("token", token);
        return modelAndView;
    }

    @GetMapping("/doctors")
    @ResponseBody
    public List<User> getDoctorsBySpecialty(@RequestParam Long specialtyId) {
        return appointmentService.getDoctorsBySpecialty(specialtyId);
    }

    @GetMapping("/dates")
    @ResponseBody
    public List<LocalDate> getAvailableDates(@RequestParam Long doctorId) {
        return appointmentService.getAvailableDates(doctorId);
    }

    @GetMapping("/slots")
    @ResponseBody
    public List<String> getAvailableTimeSlots(@RequestParam Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAvailableTimeSlots(doctorId, date);
    }

    @GetMapping("/fee")
    @ResponseBody
    public Double getConsultationFee(@RequestParam Long doctorId) {
        return appointmentService.getDoctorConsultationFee(doctorId);
    }
}