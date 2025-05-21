package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Retrieves the authenticated user from the security context.
     */
    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return availabilityService.getUserByEmail(userDetails.getUsername());
    }

    /**
     * Lists all availabilities for the authenticated doctor.
     */
    @GetMapping
    public String listAvailabilities(Model model) {
        User doctor = getAuthenticatedUser();
        if (!"DOCTOR".equals(doctor.getRole())) {
            model.addAttribute("error", "Only doctors can access this page.");
            return "redirect:/home";
        }
        List<Availability> availabilities = availabilityService.getAvailabilitiesByDoctor(doctor);
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("user", doctor);
        return "availabilities/list-availabilities";
    }

    /**
     * Displays the form to create a new availability.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User doctor = getAuthenticatedUser();
        if (!"DOCTOR".equals(doctor.getRole())) {
            model.addAttribute("error", "Only doctors can create availabilities.");
            return "redirect:/home";
        }
        model.addAttribute("availabilityDTO", new AvailabilityDTO());
        model.addAttribute("user", doctor);
        return "availabilities/create-availabilities";
    }

    /**
     * Creates a new availability.
     */
    @PostMapping
    public String createAvailability(@ModelAttribute AvailabilityDTO availabilityDTO,
                                    RedirectAttributes redirectAttributes) {
        try {
            User doctor = getAuthenticatedUser();
            if (!"DOCTOR".equals(doctor.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Only doctors can create availabilities.");
                return "redirect:/home";
            }
            Availability availability = new Availability();
            availability.setDoctor(doctor);
            availability.setDate(availabilityDTO.getDate());
            availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
            availability.setTimeSlot(availabilityDTO.getTimeSlot());
            availabilityService.createAvailability(availability);
            redirectAttributes.addFlashAttribute("message", "Availability created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/availabilities";
    }

    /**
     * Displays the form to edit an existing availability.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User doctor = getAuthenticatedUser();
        if (!"DOCTOR".equals(doctor.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Only doctors can edit availabilities.");
            return "redirect:/home";
        }
        Availability availability = availabilityService.getAvailabilityById(id);
        if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Availability not found or you do not have permission to edit it.");
            return "redirect:/availabilities";
        }
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setId(availability.getId());
        availabilityDTO.setDoctorId(doctor.getId());
        availabilityDTO.setDate(availability.getDate());
        availabilityDTO.setDayOfWeek(availability.getDayOfWeek());
        availabilityDTO.setTimeSlot(availability.getTimeSlot());
        model.addAttribute("availabilityDTO", availabilityDTO);
        model.addAttribute("user", doctor);
        return "availabilities/edit-availabilities";
    }

    /**
     * Updates an existing availability.
     */
    @PostMapping("/update/{id}")
    public String updateAvailability(@PathVariable Long id,
                                    @ModelAttribute AvailabilityDTO availabilityDTO,
                                    RedirectAttributes redirectAttributes) {
        try {
            User doctor = getAuthenticatedUser();
            if (!"DOCTOR".equals(doctor.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Only doctors can edit availabilities.");
                return "redirect:/home";
            }
            Availability availability = availabilityService.getAvailabilityById(id);
            if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Availability not found or you do not have permission to edit it.");
                return "redirect:/availabilities";
            }
            availability.setDate(availabilityDTO.getDate());
            availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
            availability.setTimeSlot(availabilityDTO.getTimeSlot());
            availabilityService.createAvailability(availability); // Reuse save for update
            redirectAttributes.addFlashAttribute("message", "Availability updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/availabilities";
    }

    /**
     * Deletes an availability.
     */
    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User doctor = getAuthenticatedUser();
            if (!"DOCTOR".equals(doctor.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Only doctors can delete availabilities.");
                return "redirect:/home";
            }
            Availability availability = availabilityService.getAvailabilityById(id);
            if (availability == null || !availability.getDoctor().getEmail().equals(doctor.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Availability not found or you do not have permission to delete it.");
                return "redirect:/availabilities";
            }
            availabilityService.deleteAvailability(id);
            redirectAttributes.addFlashAttribute("message", "Availability deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting the availability.");
        }
        return "redirect:/availabilities";
    }
}