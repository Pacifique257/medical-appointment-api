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
import org.springframework.web.servlet.ModelAndView;


@RestController
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

    // Displays the list of availabilities for the logged-in doctor.
    @GetMapping({"", "/list"})
    public ModelAndView listAvailabilities(HttpSession session, @RequestParam(value = "token", required = false) String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("GET /doctor/availability - Authentication: " + (SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().toString() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null) {
            System.out.println("Failed to load doctor for email: " + userDetails.getUsername() + ", redirecting to login");
            return new ModelAndView("redirect:/login");
        }
        if (!"DOCTOR".equals(doctor.getRole())) {
            System.out.println("User is not a DOCTOR, role: " + doctor.getRole() + ", redirecting to login");
            return new ModelAndView("redirect:/login");
        }
        List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
        if (availabilities == null) {
            System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
            availabilities = new ArrayList<>();
        }
        ModelAndView modelAndView = new ModelAndView("list-availabilities");
        modelAndView.addObject("availabilities", availabilities);
        modelAndView.addObject("user", doctor);
        modelAndView.addObject("token", token != null ? token : getTokenFromSession());
        System.out.println("Model attributes set: availabilities size=" + availabilities.size() + ", user=" + doctor.getEmail() + ", token=" + modelAndView.getModel().get("token"));
        System.out.println("Attempting to render template: list-availabilities for doctor: " + doctor.getEmail());
        return modelAndView;
    }

    // Displays the form to create a new availability.
    @GetMapping("/new")
    public ModelAndView showCreateForm(HttpSession session, @RequestParam("token") String token) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability/new - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability/new - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized access to create availability by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return new ModelAndView("redirect:/login");
        }
        String cleanToken = token.contains(",") ? token.split(",")[0] : token;
        System.out.println("Cleaned token for form: " + cleanToken);
        ModelAndView modelAndView = new ModelAndView("create-availabilities");
        modelAndView.addObject("availability", new Availability());
        modelAndView.addObject("user", doctor);
        modelAndView.addObject("token", cleanToken);
        System.out.println("Displaying create availability form for doctor: " + doctor.getEmail());
        return modelAndView;
    }
    
    // Handles the submission of a new availability.
    @PostMapping("/new")
    public ModelAndView createAvailability(
            HttpSession session,
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
            return new ModelAndView("redirect:/login");
        }
        System.out.println("Creating availability for doctor: " + doctor.getEmail() + ", date: " + date + ", dayOfWeek: " + dayOfWeek);
        System.out.println("Start times: " + startTimes);
        System.out.println("End times: " + endTimes);
        try {
            if (startTimes.size() != endTimes.size()) {
                System.out.println("Error: Mismatched start and end times");
                ModelAndView modelAndView = new ModelAndView("create-availabilities");
                modelAndView.addObject("error", "Invalid number of start and end times");
                modelAndView.addObject("user", doctor);
                modelAndView.addObject("token", cleanToken);
                return modelAndView;
            }
            LocalDate availabilityDate = LocalDate.parse(date);
            for (int i = 0; i < startTimes.size(); i++) {
                String timeSlot = startTimes.get(i) + "-" + endTimes.get(i);
                try {
                    LocalTime.parse(startTimes.get(i));
                    LocalTime.parse(endTimes.get(i));
                } catch (Exception e) {
                    System.out.println("Error: Invalid time format for slot: " + timeSlot);
                    ModelAndView modelAndView = new ModelAndView("create-availabilities");
                    modelAndView.addObject("error", "Invalid time format for slot: " + timeSlot);
                    modelAndView.addObject("user", doctor);
                    modelAndView.addObject("token", cleanToken);
                    return modelAndView;
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
            return new ModelAndView("redirect:/doctor/availability?token=" + cleanToken);
        } catch (Exception e) {
            System.out.println("Error in createAvailability: " + e.getMessage());
            ModelAndView modelAndView = new ModelAndView("create-availabilities");
            modelAndView.addObject("error", "Error: " + e.getMessage());
            modelAndView.addObject("user", doctor);
            modelAndView.addObject("token", cleanToken);
            return modelAndView;
        }
    }

    // Displays the form to edit an existing availability.
    @GetMapping("/edit/{id}")
    public ModelAndView editAvailability(@PathVariable("id") Long id, @RequestParam("token") String token, HttpSession session) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("GET /doctor/availability/edit/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("GET /doctor/availability/edit/" + id + " - URL token: " + token);
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("GET /doctor/availability/edit/" + id + " - Session token: " + sessionToken);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized access to edit availability by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return new ModelAndView("redirect:/login");
        }
        Availability availability = availabilityRepository.findById(id).orElse(null);
        if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
            System.out.println("Error retrieving availability: id=" + id + " not found or not owned by doctor: " + doctor.getEmail());
            ModelAndView modelAndView = new ModelAndView("list-availabilities");
            modelAndView.addObject("error", "Availability not found or you do not have permission to edit it");
            modelAndView.addObject("user", doctor);
            modelAndView.addObject("token", token);
            List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
            if (availabilities == null) {
                System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
                availabilities = new ArrayList<>();
            }
            modelAndView.addObject("availabilities", availabilities);
            System.out.println("Attempting to render template: list-availabilities due to error, availabilities size=" + availabilities.size());
            return modelAndView;
        }
        System.out.println("Preparing to render edit-availabilities: id=" + id + ", date=" + availability.getDate() + ", dayOfWeek=" + availability.getDayOfWeek() + ", timeSlot=" + availability.getTimeSlot());
        ModelAndView modelAndView = new ModelAndView("edit-availabilities");
        modelAndView.addObject("availability", availability);
        modelAndView.addObject("user", doctor);
        modelAndView.addObject("token", token);
        System.out.println("Attempting to render template: edit-availabilities for doctor: " + doctor.getEmail());
        return modelAndView;
    }

    // Handles the submission of an updated availability.
    @PostMapping("/update/{id}")
    public ModelAndView updateAvailability(
            @PathVariable("id") Long id,
            @RequestParam("token") String token,
            @ModelAttribute("availability") Availability availability,
            HttpSession session
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("POST /doctor/availability/update/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("POST /doctor/availability/update/" + id + " - URL token: " + token);
        System.out.println("Submitted availability: id=" + id + ", date=" + availability.getDate() + ", dayOfWeek=" + availability.getDayOfWeek() + ", timeSlot=" + availability.getTimeSlot());
        User doctor = userService.getUserByEmail(userDetails.getUsername());
        String sessionToken = (String) session.getAttribute("accessToken");
        System.out.println("POST /doctor/availability/update/" + id + " - Session token: " + sessionToken);
        System.out.println("Doctor: " + (doctor != null ? doctor.getEmail() + ", role: " + doctor.getRole() : "null"));
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Unauthorized submission to update availability by email: " + userDetails.getUsername());
            return new ModelAndView("redirect:/login");
        }
        Availability existingAvailability = availabilityRepository.findById(id).orElse(null);
        if (existingAvailability == null || !existingAvailability.getDoctor().getEmail().equals(doctor.getEmail())) {
            System.out.println("Error retrieving availability: id=" + id + " not found or not owned by doctor: " + doctor.getEmail());
            ModelAndView modelAndView = new ModelAndView("list-availabilities");
            modelAndView.addObject("error", "Availability not found or you do not have permission to update it");
            modelAndView.addObject("user", doctor);
            modelAndView.addObject("token", token);
            List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
            if (availabilities == null) {
                System.out.println("Availabilities is null for doctor: " + doctor.getEmail() + ", setting to empty list");
                availabilities = new ArrayList<>();
            }
            modelAndView.addObject("availabilities", availabilities);
            System.out.println("Attempting to render template: list-availabilities due to error, availabilities size=" + availabilities.size());
            return modelAndView;
        }
        try {
            existingAvailability.setDate(availability.getDate());
            existingAvailability.setDayOfWeek(availability.getDayOfWeek());
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
                    ", timeSlot=" + existingAvailability.getTimeSlot());
            return new ModelAndView("redirect:/doctor/availability?token=" + token);
        } catch (Exception e) {
            System.out.println("Error saving availability: id=" + id + ", error=" + e.getMessage());
            ModelAndView modelAndView = new ModelAndView("edit-availabilities");
            modelAndView.addObject("error", "Error updating availability: " + e.getMessage());
            modelAndView.addObject("user", doctor);
            modelAndView.addObject("token", token);
            modelAndView.addObject("availability", existingAvailability);
            System.out.println("Attempting to render template: edit-availabilities due to error");
            return modelAndView;
        }
    }

    // Handles the deletion of an availability.
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

    // Helper method to get token from session (adjust as needed).
    private String getTokenFromSession() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}