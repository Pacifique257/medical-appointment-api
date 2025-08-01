package com.example.medical_appointment.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class UserDTO {

    @NotBlank(message = "Le nom est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastName;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le rôle est requis")
    @Pattern(regexp = "ADMIN|DOCTOR|PATIENT", message = "Le rôle doit être ADMIN, DOCTOR ou PATIENT")
    private String role;

    @NotBlank(message = "Le téléphone est requis")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String phone;

    @NotBlank(message = "La date de naissance est requise")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "La date de naissance doit être au format YYYY-MM-DD")
    private String birthDate;

    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
    private String address;

    @Min(value = 0, message = "Les frais de consultation doivent être positifs")
    private Double consultationFee;

    @Size(max = 1000, message = "La biographie ne doit pas dépasser 1000 caractères")
    private String biography;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Le genre doit être MALE, FEMALE ou OTHER")
    private String gender;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caractères")
    private String password;

    private MultipartFile profilePictureFile;

    @Positive(message = "L'ID de la spécialité doit être positif")
    private Long specialtyId;

    // Getters et setters
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public MultipartFile getProfilePictureFile() { return profilePictureFile; }
    public void setProfilePictureFile(MultipartFile profilePictureFile) { this.profilePictureFile = profilePictureFile; }
    public Long getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Long specialtyId) { this.specialtyId = specialtyId; }
}