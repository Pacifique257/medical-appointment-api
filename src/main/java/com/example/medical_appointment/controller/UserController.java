package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SpecialtyRepository specialtyRepository;

    @Autowired
    public UserController(UserService userService,SpecialtyRepository specialtyRepository) {
        this.userService = userService;
        this.specialtyRepository = specialtyRepository;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        System.out.println(">>> createUser called");
        try {
            User user = new User();
            user.setLastName(userDTO.getLastName());
            user.setFirstName(userDTO.getFirstName());
            user.setEmail(userDTO.getEmail());
            user.setRole(userDTO.getRole());
            user.setPhone(userDTO.getPhone());
            user.setBirthDate(userDTO.getBirthDate());
            user.setAddress(userDTO.getAddress());
            user.setConsultationFee(userDTO.getConsultationFee());
            user.setBiography(userDTO.getBiography());
            user.setGender(userDTO.getGender());
            user.setProfilePicture(userDTO.getProfilePicture());
            user.setPassword(userDTO.getPassword());

            if (userDTO.getSpecialtyId() != null) {
                Specialty specialty = new Specialty();
                specialty.setId(userDTO.getSpecialtyId());
                user.setSpecialty(specialty);
            }

            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }


    // Display the update form
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "error"; // Assumes error.html exists
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setConsultationFee(user.getConsultationFee());
        userDTO.setBiography(user.getBiography());
        userDTO.setSpecialtyId(user.getSpecialty() != null ? user.getSpecialty().getId() : null);

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("userId", id);
        model.addAttribute("userRole", user.getRole());

        if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
            List<Specialty> specialties = specialtyRepository.findAll();
            model.addAttribute("specialties", specialties);
        }

        return "update-user"; // Nom du fichier .html dans templates/
    }
    // Update user details with file upload
    @PostMapping(value = "/{id}", consumes = "multipart/form-data")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserDTO userDTO) {
        System.out.println(">>> updateUser called for ID: " + id);
        try {
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return "error"; // Assumes an error.html page exists
            }
            String userRole = existingUser.getRole();
            userService.updateUser(id, userDTO, userRole);
            return "redirect:/api/users/" + id; // Redirect to profile page
        } catch (IllegalArgumentException e) {
            return "error"; // Assumes an error.html page exists
        }
    }

    // Display user profile
    @GetMapping("/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "error"; // Assumes an error.html page exists
        }
        model.addAttribute("user", user);
        return "profile";
    }        
    
 
@GetMapping("/profile")
public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.getUserByEmail(userDetails.getUsername());
    if (user == null) {
        return "error";
    }

    UserDTO userDTO = new UserDTO();
    userDTO.setLastName(user.getLastName());
    userDTO.setFirstName(user.getFirstName());
    userDTO.setEmail(user.getEmail());
    userDTO.setRole(user.getRole());
    userDTO.setPhone(user.getPhone());
    userDTO.setBirthDate(user.getBirthDate());
    userDTO.setAddress(user.getAddress());
    userDTO.setGender(user.getGender());
    userDTO.setProfilePicture(user.getProfilePicture());
    userDTO.setBiography(user.getBiography());
    userDTO.setConsultationFee(user.getConsultationFee());

    if (user.getSpecialty() != null) {
        userDTO.setSpecialtyId(user.getSpecialty().getId());
        userDTO.setSpecialtyName(user.getSpecialty().getName());  // ✅ ici on utilise le nom
    }

    model.addAttribute("user", userDTO);
    return "profile";
}


    
    
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(user);
    }
}


