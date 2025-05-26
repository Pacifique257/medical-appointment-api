package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AppointmentService;
import com.example.medical_appointment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
        System.out.println("GET /api/appointments/create - Rendering appointment-form for patient ID: " + patient.getId());
        return modelAndView;
    }

@PostMapping("/create")
public ModelAndView createAppointment(@ModelAttribute AppointmentDTO appointmentDTO, 
                                     @RequestParam(value = "token", required = false) String token) {
    System.out.println("POST /api/appointments/create - Starting request processing, token: " + token);
    System.out.println("POST /api/appointments/create - AppointmentDTO: " + appointmentDTO);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() || 
        authentication.getPrincipal().equals("anonymousUser")) {
        System.out.println("POST /api/appointments/create - User not authenticated, redirecting to login");
        return new ModelAndView("redirect:/login");
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    try {
        Appointment appointment = appointmentService.createAppointment(appointmentDTO, userDetails.getUsername());
        System.out.println("POST /api/appointments/create - Appointment created successfully: ID=" + appointment.getId());
        return new ModelAndView("redirect:/api/appointments/patient?token=" + token);
    } catch (Exception e) {
        System.out.println("POST /api/appointments/create - Error: " + e.getMessage());
        e.printStackTrace();
        ModelAndView modelAndView = new ModelAndView("appointment-form");
        modelAndView.addObject("error", "Failed to create appointment: " + e.getMessage());
        modelAndView.addObject("appointmentDTO", appointmentDTO);
        modelAndView.addObject("specialties", appointmentService.getAllSpecialties());
        modelAndView.addObject("token", token);
        return modelAndView;
    }
}

@GetMapping("/patient")
    public ModelAndView getPatientAppointments(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/appointments/patient - Starting request processing, token: " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/appointments/patient - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Fetching appointments for user: " + userDetails.getUsername());

        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatientEmail(userDetails.getUsername());
            System.out.println("Found " + appointments.size() + " appointments for user: " + userDetails.getUsername());
            ModelAndView modelAndView = new ModelAndView("patient-appointments");
            modelAndView.addObject("appointments", appointments);
            modelAndView.addObject("token", token);
            return modelAndView;
        } catch (Exception e) {
            System.out.println("GET /api/appointments/patient - Error: " + e.getMessage());
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("patient-appointments");
            modelAndView.addObject("error", "Failed to load appointments: " + e.getMessage());
            modelAndView.addObject("appointments", new ArrayList<>());
            modelAndView.addObject("token", token);
            return modelAndView;
        }
    }

    @GetMapping("/doctor/view")
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
        System.out.println("GET /api/appointments/doctors - Fetching doctors for specialtyId: " + specialtyId);
        return appointmentService.getDoctorsBySpecialty(specialtyId);
    }

    @GetMapping("/dates")
    @ResponseBody
    public List<LocalDate> getAvailableDates(@RequestParam Long doctorId) {
         System.out.println("GET /api/appointments/dates - Fetching dates for doctorId: " + doctorId);
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
    @GetMapping("/availability")
    @ResponseBody
    public AvailabilityDTO getAvailability(@RequestParam Long doctorId, 
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, 
                                          @RequestParam String timeSlot) {
        System.out.println("GET /api/appointments/availability - doctorId: " + doctorId + 
                          ", date: " + date + ", timeSlot: " + timeSlot);
        try {
            AvailabilityDTO availability = appointmentService.getAvailability(doctorId, date, timeSlot);
            System.out.println("GET /api/appointments/availability - Success, ID: " + availability.getId());
            return availability;
        } catch (Exception e) {
            System.out.println("GET /api/appointments/availability - Error: " + e.getMessage());
            throw e;
        }
    }
  
@PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long appointmentId, 
                                   @RequestParam(value = "token", required = false) String token) {
        System.out.println("POST /api/appointments/cancel/" + appointmentId + ", token: " + token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("POST /api/appointments/cancel/" + appointmentId + " - User not authenticated");
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            appointmentService.cancelAppointment(appointmentId, userDetails.getUsername());
            System.out.println("Appointment ID: " + appointmentId + " cancelled successfully");
        } catch (Exception e) {
            System.out.println("POST /api/appointments/cancel/" + appointmentId + " - Error: " + e.getMessage());
        }
        return "redirect:/api/appointments/patient?token=" + token;
    }

    @PostMapping("/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Long appointmentId, 
                                    @RequestParam(value = "token", required = false) String token) {
        System.out.println("POST /api/appointments/confirm/" + appointmentId + ", token: " + token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("POST /api/appointments/confirm/" + appointmentId + " - User not authenticated");
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            appointmentService.confirmAppointment(appointmentId, userDetails.getUsername());
            System.out.println("Appointment ID: " + appointmentId + " confirmed successfully");
        } catch (Exception e) {
            System.out.println("POST /api/appointments/confirm/" + appointmentId + " - Error: " + e.getMessage());
        }
        return "redirect:/api/appointments/doctor?token=" + token;
    }

    @PostMapping("/complete/{id}")
    public String completeAppointment(@PathVariable("id") Long appointmentId, 
                                     @RequestParam(value = "token", required = false) String token) {
        System.out.println("POST /api/appointments/complete/" + appointmentId + ", token: " + token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("POST /api/appointments/complete/" + appointmentId + " - User not authenticated");
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            appointmentService.completeAppointment(appointmentId, userDetails.getUsername());
            System.out.println("Appointment ID: " + appointmentId + " completed successfully");
        } catch (Exception e) {
            System.out.println("POST /api/appointments/complete/" + appointmentId + " - Error: " + e.getMessage());
        }
        return "redirect:/api/appointments/doctor?token=" + token;
    }

    @GetMapping("/doctor")
    public ModelAndView getDoctorAppointments(@RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/appointments/doctor - Starting request processing, token: " + token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/appointments/doctor - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Fetching appointments for doctor: " + userDetails.getUsername());
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorEmail(userDetails.getUsername());
            System.out.println("Found " + appointments.size() + " appointments for doctor: " + userDetails.getUsername());
            ModelAndView modelAndView = new ModelAndView("doctor-appointments");
            modelAndView.addObject("appointments", appointments);
            modelAndView.addObject("token", token);
            return modelAndView;
        } catch (Exception e) {
            System.out.println("GET /api/appointments/doctor - Error: " + e.getMessage());
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("doctor-appointments");
            modelAndView.addObject("error", "Failed to load appointments: " + e.getMessage());
            modelAndView.addObject("appointments", new ArrayList<>());
            modelAndView.addObject("token", token);
            return modelAndView;
        }
    }
}