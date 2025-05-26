package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private AppointmentService appointmentService;
@GetMapping("/doctor")
public ModelAndView doctorDashboard(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /doctor - Starting request processing");
        System.out.println("GET /doctor - Received token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /doctor - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        long appointmentCount = appointmentService.countDoctorAppointments(email);
        List<Appointment> appointments = appointmentService.getNextDoctorAppointments(email);

        ModelAndView modelAndView = new ModelAndView("doctor-dashboard");
        modelAndView.addObject("appointmentCount", appointmentCount);
        modelAndView.addObject("appointments", appointments);
        modelAndView.addObject("userEmail", email);
        modelAndView.addObject("token", token);
        System.out.println("Doctor dashboard loaded for " + email + ": " + appointmentCount + " appointments");
        return modelAndView;
    }

    @GetMapping("/patient")
    public ModelAndView patientDashboard(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /patient - Starting request processing");
        System.out.println("GET /patient - Received token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /patient - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        long appointmentCount = appointmentService.countPatientAppointments(email);
        List<Appointment> appointments = appointmentService.getNextPatientAppointments(email);

        ModelAndView modelAndView = new ModelAndView("patient-dashboard");
        modelAndView.addObject("appointmentCount", appointmentCount);
        modelAndView.addObject("appointments", appointments);
        modelAndView.addObject("userEmail", email);
        modelAndView.addObject("token", token);
        System.out.println("Patient dashboard loaded for " + email + ": " + appointmentCount + " appointments");
        return modelAndView;
    }
}