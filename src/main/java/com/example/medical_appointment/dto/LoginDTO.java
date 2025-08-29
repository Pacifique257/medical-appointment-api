package com.example.medical_appointment.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
=======


import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}