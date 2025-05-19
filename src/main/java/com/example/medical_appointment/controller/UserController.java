package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
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
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
