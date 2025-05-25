package com.example.medical_appointment.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id; // Ajout de la propriété id
    private String lastName;
    private String firstName;
    private String email;
    private String role;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private Double consultationFee;
    private String biography;
    private String gender;
    private String profilePicture;
    private MultipartFile profilePictureFile;
    private Long specialtyId;
    private String specialtyName;
    private String password;
}