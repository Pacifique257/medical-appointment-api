package com.example.medical_appointment.Models;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;



@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "biography")
    private String biography;

    @Column(name = "gender")
    private String gender;

    @Column(name = "profile_picture")
    private String profilePicture;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    @Column(name = "password", nullable = false)
    private String password;
}