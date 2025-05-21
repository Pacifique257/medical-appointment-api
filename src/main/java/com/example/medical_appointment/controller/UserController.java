package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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


