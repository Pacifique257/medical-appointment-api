package com.example.medical_appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialtyDTO {
    private Long id;

    @NotBlank(message = "Le nom de la spécialité est requis")
    @Size(max = 100, message = "Le nom de la spécialité ne doit pas dépasser 100 caractères")
    private String name;
}
