package com.example.medical_appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityDTO {
    private Long id;

    @NotNull(message = "L'ID du docteur est requis")
    private Long doctorId;

    @NotNull(message = "La date est requise")
    private LocalDate date;

    @NotBlank(message = "Le jour de la semaine est requis")
    private String dayOfWeek;

    @NotBlank(message = "Le créneau horaire est requis")
    private String timeSlot;
}