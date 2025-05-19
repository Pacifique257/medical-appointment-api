package com.example.medical_appointment.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

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
    private Long specialtyId;
    private String password;
}