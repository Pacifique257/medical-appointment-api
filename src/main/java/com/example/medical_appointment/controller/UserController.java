package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView showUpdateForm(@PathVariable Long id, HttpSession session, @RequestParam(value = "token", required = false) String token) {
        System.out.println("GET /api/users/" + id + "/edit - Starting request processing");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("GET /api/users/" + id + "/edit - Authentication: " + (authentication != null ? authentication.toString() : "null"));
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /api/users/" + id + "/edit - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("GET /api/users/" + id + "/edit - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));

        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        System.out.println("GET /api/users/" + id + "/edit - Authenticated user: " + (authenticatedUser != null ? authenticatedUser.getEmail() + ", role: " + authenticatedUser.getRole() : "null"));
        if (authenticatedUser == null || !authenticatedUser.getId().equals(id)) {
            System.out.println("GET /api/users/" + id + "/edit - Unauthorized access by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(id);
        if (user == null) {
            System.out.println("GET /api/users/" + id + "/edit - User not found, id: " + id);
            return new ModelAndView("error");
        }
        System.out.println("GET /api/users/" + id + "/edit - Found user: " + user.getEmail() + ", role: " + user.getRole());

        UserDTO userDTO = new UserDTO();
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setConsultationFee(user.getConsultationFee());
        userDTO.setBiography(user.getBiography());
        userDTO.setSpecialtyId(user.getSpecialty() != null ? user.getSpecialty().getId() : null);
        System.out.println("GET /api/users/" + id + "/edit - UserDTO created: biography=" + userDTO.getBiography() + ", consultationFee=" + userDTO.getConsultationFee());

        ModelAndView modelAndView = new ModelAndView("update-user");
        modelAndView.addObject("userDTO", userDTO);
        modelAndView.addObject("userId", id);
        modelAndView.addObject("userRole", user.getRole());
        System.out.println("GET /api/users/" + id + "/edit - Model attributes set: userId=" + id + ", userRole=" + user.getRole());

        if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
            List<Specialty> specialties = specialtyRepository.findAll();
            modelAndView.addObject("specialties", specialties);
            System.out.println("GET /api/users/" + id + "/edit - Specialties added to model, count: " + specialties.size());
        }

        String sessionToken = (String) session.getAttribute("accessToken");
        modelAndView.addObject("token", token != null ? token : sessionToken);
        System.out.println("GET /api/users/" + id + "/edit - Session token: " + sessionToken + ", URL token: " + token);
        System.out.println("GET /api/users/" + id + "/edit - Attempting to render template: update-user for user id: " + id);

        return modelAndView;
    }

    // Update user details with file upload
@PostMapping(value = "/{id}", consumes = "multipart/form-data")
public ModelAndView updateUser(@PathVariable Long id, @ModelAttribute UserDTO userDTO, HttpSession session, @RequestParam(value = "token", required = false) String token) {
    System.out.println("POST /api/users/" + id + " - Starting updateUser for ID: " + id);
    System.out.println("POST /api/users/" + id + " - Received token from URL: " + token);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("POST /api/users/" + id + " - Authentication: " + (authentication != null ? authentication.toString() : "null"));
    if (authentication == null || !authentication.isAuthenticated() || 
        authentication.getPrincipal().equals("anonymousUser")) {
        System.out.println("POST /api/users/" + id + " - User not authenticated, redirecting to login");
        return new ModelAndView("redirect:/login");
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    System.out.println("POST /api/users/" + id + " - UserDetails username: " + (userDetails != null ? userDetails.getUsername() : "null"));

    User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
    System.out.println("POST /api/users/" + id + " - Authenticated user: " + (authenticatedUser != null ? authenticatedUser.getEmail() + ", role: " + authenticatedUser.getRole() : "null"));
    if (authenticatedUser == null || !authenticatedUser.getId().equals(id)) {
        System.out.println("POST /api/users/" + id + " - Unauthorized access by email: " + (userDetails != null ? userDetails.getUsername() : "null"));
        return new ModelAndView("redirect:/login");
    }

    try {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            System.out.println("POST /api/users/" + id + " - User not found, id: " + id);
            return new ModelAndView("error");
        }
        System.out.println("POST /api/users/" + id + " - Found existing user: " + existingUser.getEmail() + ", role: " + existingUser.getRole());
        System.out.println("POST /api/users/" + id + " - Profile picture file: " + (userDTO.getProfilePictureFile() != null ? userDTO.getProfilePictureFile().getOriginalFilename() : "none"));

        String userRole = existingUser.getRole();
        userService.updateUser(id, userDTO, userRole);
        System.out.println("POST /api/users/" + id + " - User updated successfully, redirecting to profile");
        // Rediriger avec le token
        return new ModelAndView("redirect:/api/users/profile?token=" + token);
    } catch (IllegalArgumentException e) {
        System.out.println("POST /api/users/" + id + " - Error updating user: " + e.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "Error: " + e.getMessage());
        modelAndView.addObject("user", authenticatedUser);
        modelAndView.addObject("token", token);
        System.out.println("POST /api/users/" + id + " - Rendering error template with message: " + e.getMessage());
        return modelAndView;
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
public ModelAndView showProfile(@RequestParam(value = "token", required = false) String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("GET /profile - User not authenticated, redirecting to login");
            return new ModelAndView("redirect:/login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("GET /profile - UserDetails: " + userDetails.getUsername());

        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user == null) {
            System.out.println("GET /profile - User not found, email: " + userDetails.getUsername());
            return new ModelAndView("error");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId()); // Ajout de l'ID
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
            userDTO.setSpecialtyName(user.getSpecialty().getName());
        }

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", userDTO);
        modelAndView.addObject("token", token);

        System.out.println("GET /profile - Profile loaded for: " + user.getEmail());
        System.out.println("ModelAndView attributes: " + modelAndView.getModel());

        return modelAndView;
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


