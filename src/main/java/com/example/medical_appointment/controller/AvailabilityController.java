package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import com.example.medical_appointment.service.AvailabilityService;
import com.example.medical_appointment.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService, UserService userService, AvailabilityRepository availabilityRepository) {
        this.availabilityService = availabilityService;
        this.userService = userService;
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Displays the list of availabilities for the logged-in doctor.
     */
    @GetMapping({"", "/list"})
    public String listAvailabilities(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("GET /doctor/availability - Authentication: " + (SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().toString() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null) {
            System.out.println("Failed to load doctor for email: " + userDetails.getUsername() + ", redirecting to login");
            return "redirect:/login";
        }
        if (!"DOCTOR".equals(doctor.getRole())) {
            System.out.println("User is not a DOCTOR, role: " + doctor.getRole() + ", redirecting to login");
            return "redirect:/login";
        }
        List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
        if (availabilities == null) {
            System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
            availabilities = new ArrayList<>();
        }
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("user", doctor);
        model.addAttribute("token", token != null ? token : getTokenFromSession());
        System.out.println("Model attributes set: availabilities size=" + availabilities.size() + ", user=" + doctor.getEmail() + ", token=" + model.asMap().get("token"));
        System.out.println("Attempting to render template: list-availabilities for doctor: " + doctor.getEmail());
        return "list-availabilities";
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
     * Displays the form to edit an existing availability.
     */
    @GetMapping("/edit/{id}")
    public String editAvailability(@PathVariable("id") Long id, @RequestParam("token") String token, Model model, HttpSession session) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability/edit/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("GET /doctor/availability/edit/" + id + " - URL token: " + token);
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability/edit/" + id + " - Session token: " + sessionToken);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized access to edit availability by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return "redirect:/login";
        }
        Availability availability = availabilityRepository.findById(id).orElse(null);
        if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
            System.out.println("Error retrieving availability: id=" + id + " not found or not owned by doctor: " + doctor.getEmail());
            model.addAttribute("error", "Availability not found or you do not have permission to edit it");
            model.addAttribute("user", doctor);
            model.addAttribute("token", token);
            List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
            if (availabilities == null) {
                System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
                availabilities = new ArrayList<>();
            }
            model.addAttribute("availabilities", availabilities);
            System.out.println("Attempting to render template: list-availabilities due to error, availabilities size=" + availabilities.size());
            return "list-availabilities";
        }
        System.out.println("Retrieved availability: id=" + id +
                ", date=" + (availability.getDate() != null ? availability.getDate() : "null") +
                ", dayOfWeek=" + (availability.getDayOfWeek() != null ? availability.getDayOfWeek() : "null") +
                ", timeSlot=" + (availability.getTimeSlot() != null ? availability.getTimeSlot() : "null"));
        // Check for null values
        if (availability.getDate() == null) {
            System.out.println("Warning: date is null for availability id=" + id);
            model.addAttribute("error", "Availability date is missing");
            return "list-availabilities";
        }
        if (availability.getDayOfWeek() == null) {
            System.out.println("Warning: dayOfWeek is null for availability id=" + id);
            model.addAttribute("error", "Availability day of week is missing");
            return "list-availabilities";
        }
        // Verify dayOfWeek consistency
        String expectedDayOfWeek = availability.getDate().getDayOfWeek().toString();
        if (!expectedDayOfWeek.equals(availability.getDayOfWeek())) {
            System.out.println("Warning: dayOfWeek inconsistency for id=" + id + ", database dayOfWeek=" + availability.getDayOfWeek() + ", expected=" + expectedDayOfWeek);
        }
        model.addAttribute("availability", availability);
        model.addAttribute("user", doctor);
        model.addAttribute("token", token);
        System.out.println("Attempting to render template: edit-availabilities for doctor: " + doctor.getEmail());
        return "edit-availabilities";
    }

    /**
     * Handles the submission of an updated availability.
     */
    @PostMapping("/update/{id}")
    public String updateAvailability(
            @PathVariable("id") Long id,
            @RequestParam("token") String token,
            @RequestParam("date") String date,
            @RequestParam("dayOfWeek") String dayOfWeek,
            @ModelAttribute("availability") Availability availability,
            Model model,
            HttpSession session
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("POST /doctor/availability/update/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("POST /doctor/availability/update/" + id + " - URL token: " + token);
        System.out.println("Submitted values: id=" + id + ", date=" + date + ", dayOfWeek=" + dayOfWeek + ", timeSlot=" + availability.getTimeSlot());
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("POST /doctor/availability/update/" + id + " - Session token: " + sessionToken);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized submission to update availability by email: " + userDetails.getUsername());
            return "redirect:/login";
        }
        Availability existingAvailability = availabilityRepository.findById(id).orElse(null);
        if (existingAvailability == null || !existingAvailability.getDoctor().getEmail().equals(doctor.getEmail())) {
            System.out.println("Error retrieving availability: id=" + id + " not found or not owned by doctor: " + doctor.getEmail());
            model.addAttribute("error", "Availability not found or you do not have permission to update it");
            model.addAttribute("user", doctor);
            model.addAttribute("token", token);
            List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
            if (availabilities == null) {
                System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
                availabilities = new ArrayList<>();
            }
            model.addAttribute("availabilities", availabilities);
            System.out.println("Attempting to render template: list-availabilities due to error, availabilities size=" + availabilities.size());
            return "list-availabilities";
        }
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            // Validate dayOfWeek consistency
            String expectedDayOfWeek = parsedDate.getDayOfWeek().toString();
            if (!expectedDayOfWeek.equals(dayOfWeek)) {
                System.out.println("Error: Submitted dayOfWeek=" + dayOfWeek + " does not match date=" + parsedDate + ", expected=" + expectedDayOfWeek);
                throw new IllegalArgumentException("Day of week does not match the selected date");
            }
            existingAvailability.setDate(parsedDate);
            existingAvailability.setDayOfWeek(dayOfWeek);
            existingAvailability.setTimeSlot(availability.getTimeSlot());
            // Validate timeSlot format (e.g., "09:00-10:00")
            String[] times = availability.getTimeSlot().split("-");
            if (times.length != 2) {
                throw new IllegalArgumentException("Invalid time slot format");
            }
            LocalTime.parse(times[0]);
            LocalTime.parse(times[1]);
            availabilityRepository.save(existingAvailability);
            System.out.println("Saved availability: doctor=" + existingAvailability.getDoctor().getEmail() +
                    ", date=" + existingAvailability.getDate() +
                    ", dayOfWeek=" + existingAvailability.getDayOfWeek() +
                    ", timeSlot=" + existingAvailability.getTimeSlot());
            return "redirect:/doctor/availability?token=" + token;
        } catch (Exception e) {
            System.out.println("Error saving availability: id=" + id + ", error=" + e.getMessage());
            model.addAttribute("error", "Error updating availability: " + e.getMessage());
            model.addAttribute("user", doctor);
            model.addAttribute("token", token);
            model.addAttribute("availability", existingAvailability);
            System.out.println("Attempting to render template: edit-availabilities due to error");
            return "edit-availabilities";
        }
    }

    /**
     * Handles the deletion of an availability.
     */
    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable("id") Long id, @RequestParam("token") String token, HttpSession session) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("POST /doctor/availability/delete/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("POST /doctor/availability/delete/" + id + " - URL token: " + token);
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("POST /doctor/availability/delete/" + id + " - Session token: " + sessionToken);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized deletion of availability by email: " + userDetails.getUsername());
            return "redirect:/login";
        }
        Availability availability = availabilityRepository.findById(id).orElse(null);
        if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
            System.out.println("Error retrieving availability: id=" + id + " not found or not owned by doctor: " + doctor.getEmail());
            return "redirect:/doctor/availability?token=" + token;
        }
        try {
            availabilityRepository.deleteById(id);
            System.out.println("Deleted availability: id=" + id);
            return "redirect:/doctor/availability?token=" + token;
        } catch (Exception e) {
            System.out.println("Error deleting availability: id=" + id + ", error=" + e.getMessage());
            return "redirect:/doctor/availability?token=" + token;
        }
    }

    /**
     * Helper method to get token from session (adjust as needed).
     */
    private String getTokenFromSession() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}