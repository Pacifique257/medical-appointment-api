package com.example.medical_appointment.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
=======


>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityDTO {
    private Long id;
<<<<<<< HEAD

    @NotNull(message = "L'ID du docteur est requis")
    private Long doctorId;

    @NotNull(message = "La date est requise")
    private LocalDate date;

    @NotBlank(message = "Le jour de la semaine est requis")
    private String dayOfWeek;

    @NotBlank(message = "Le créneau horaire est requis")
=======
    private Long doctorId;
    private LocalDate date;
    private String dayOfWeek;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    private String timeSlot;
}