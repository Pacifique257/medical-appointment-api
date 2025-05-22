package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.service.AvailabilityService;
import com.example.medical_appointment.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/doctor/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final UserService userService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService, UserService userService) {
        this.availabilityService = availabilityService;
        this.userService = userService;
    }

    /**
     * Displays the list of availabilities for the logged-in doctor.
     */
    @GetMapping({"", "/list"})
    public String listAvailabilities(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized access to availabilities by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return "redirect:/login";
        }
        List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
        System.out.println("Retrieved " + availabilities.size() + " availabilities for doctor: " + doctor.getEmail());
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("user", doctor);
        model.addAttribute("token", token != null ? token : getTokenFromSession());
        System.out.println("Attempting to render template: availabilities/list-availabilities");
        return "availabilities/list-availabilities";
    }

    /**
     * Displays the form to create a new availability.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session, @RequestParam("token") String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability/new - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability/new - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized access to create availability by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return "redirect:/login";
        }
        String cleanToken = token.contains(",") ? token.split(",")[0] : token;
        System.out.println("Cleaned token for form: " + cleanToken);
        model.addAttribute("availability", new Availability());
        model.addAttribute("user", doctor);
        model.addAttribute("token", cleanToken);
        System.out.println("Displaying create availability form for doctor: " + doctor.getEmail());
        return "create-availabilities";
    }

    /**
     * Handles the submission of a new availability.
     */
    @PostMapping("/new")
    public String createAvailability(
            HttpSession session,
            Model model,
            @RequestParam("token") String token,
            @RequestParam("date") String date,
            @RequestParam("dayOfWeek") String dayOfWeek,
            @RequestParam("startTimes") List<String> startTimes,
            @RequestParam("endTimes") List<String> endTimes
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("POST /doctor/availability/new - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("POST /doctor/availability/new - Session token: " + sessionToken + ", Form token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        String cleanToken = token.contains(",") ? token.split(",")[0] : token;
        System.out.println("Cleaned token for redirection: " + cleanToken);
        System.out.println("UserDetails: " + userDetails.getUsername() + ", Role: " + (doctor != null ? doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized submission of availability by email: " + userDetails.getUsername());
            return "redirect:/login";
        }
        System.out.println("Creating availability for doctor: " + doctor.getEmail() + ", date: " + date + ", dayOfWeek: " + dayOfWeek);
        System.out.println("Start times: " + startTimes);
        System.out.println("End times: " + endTimes);
        try {
            if (startTimes.size() != endTimes.size()) {
                model.addAttribute("error", "Invalid number of start and end times");
                System.out.println("Error: Mismatched start and end times");
                model.addAttribute("user", doctor);
                model.addAttribute("token", cleanToken);
                return "create-availabilities";
            }
            LocalDate availabilityDate = LocalDate.parse(date);
            for (int i = 0; i < startTimes.size(); i++) {
                String timeSlot = startTimes.get(i) + "-" + endTimes.get(i);
                try {
                    LocalTime.parse(startTimes.get(i));
                    LocalTime.parse(endTimes.get(i));
                } catch (Exception e) {
                    model.addAttribute("error", "Invalid time format for slot: " + timeSlot);
                    System.out.println("Error: Invalid time format for slot: " + timeSlot);
                    model.addAttribute("user", doctor);
                    model.addAttribute("token", cleanToken);
                    return "create-availabilities";
                }
                Availability availability = new Availability();
                availability.setDoctor(doctor);
                availability.setDate(availabilityDate);
                availability.setDayOfWeek(dayOfWeek);
                availability.setTimeSlot(timeSlot);
                availabilityService.createAvailability(availability);
                System.out.println("Created availability: date=" + date + ", timeSlot=" + timeSlot);
            }
            System.out.println("All availabilities created successfully for doctor: " + doctor.getEmail());
            System.out.println("Redirecting with token: " + cleanToken);
            return "redirect:/doctor/availability?token=" + cleanToken;
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error in createAvailability: " + e.getMessage());
            model.addAttribute("user", doctor);
            model.addAttribute("token", cleanToken);
            return "create-availabilities";
        }
    }

    /**
     * Placeholder for editing an availability (to be implemented).
     */
    @GetMapping("/edit/{id}")
    public String editAvailability(@PathVariable("id") Long id, @RequestParam("token") String token, Model model) {
        System.out.println("Edit availability requested for ID: " + id + ", token: " + token);
        return "redirect:/doctor/availability?token=" + token;
    }

    /**
     * Placeholder for deleting an availability (to be implemented).
     */
    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable("id") Long id, @RequestParam("token") String token) {
        System.out.println("Delete availability requested for ID: " + id + ", token: " + token);
        return "redirect:/doctor/availability?token=" + token;
    }

    /**
     * Helper method to get token from session (adjust as needed).
     */
    private String getTokenFromSession() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
 