package com.example.medical_appointment.Models;

<<<<<<< HEAD
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
=======


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

//@Data
//@Entity
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "last_name", nullable = false)
//    private String lastName;
//
//    @Column(name = "first_name", nullable = false)
//    private String firstName;
//
//    @Column(name = "email", nullable = false, unique = true)
//    private String email;
//
//    @Column(name = "role", nullable = false)
//    private String role;
//
//    @Column(name = "phone", nullable = false)
//    private String phone;
//
//    @Column(name = "birth_date", nullable = false)
//    private LocalDate birthDate;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "consultation_fee")
//    private Double consultationFee;
//
//    @Column(name = "biography", length = 1000)
//    private String biography;
//
//    @Column(name = "gender")
//    private String gender;
//
//    @Column(name = "profile_picture")
//    private String profilePicture;
//
//    @ManyToOne
//    @JoinColumn(name = "specialty_id")
//    private Specialty specialty;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//}



@Entity
@Table(name = "users")
@Data
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birth_date")
=======
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
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "biography")
    private String biography;

    @Column(name = "gender")
    private String gender;

<<<<<<< HEAD
    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

=======
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    @Column(name = "profile_picture")
    private String profilePicture;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

<<<<<<< HEAD
    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
=======
    @Column(name = "password", nullable = false)
    private String password;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}