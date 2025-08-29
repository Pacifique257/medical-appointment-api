package com.example.medical_appointment.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
=======

>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
import lombok.Data;

@Data
public class SpecialtyDTO {
    private Long id;
<<<<<<< HEAD

    @NotBlank(message = "Le nom de la spécialité est requis")
    @Size(max = 100, message = "Le nom de la spécialité ne doit pas dépasser 100 caractères")
    private String name;
}
=======
    private String name;
}
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
