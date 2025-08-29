package com.example.medical_appointment.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
=======


>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long id;
<<<<<<< HEAD

    @NotNull(message = "L'ID du patient est requis")
    private Long patientId;

    @NotNull(message = "L'ID du docteur est requis")
    private Long doctorId;

    @NotNull(message = "L'ID de la disponibilité est requis")
    private Long availabilityId;

    @NotNull(message = "La date du rendez-vous est requise")
    private LocalDate appointmentDate;

    @NotBlank(message = "Le jour est requis")
    private String day;

    @NotBlank(message = "Le créneau horaire est requis")
    private String timeSlot;

    @NotBlank(message = "La raison est requise")
    private String reason;

    @NotNull(message = "Le coût de la consultation est requis")
    private Double consultationFee;

=======
    private Long patientId;
    private Long doctorId;
    private Long availabilityId;
    private LocalDate appointmentDate;
    private String day;
    private String reason;
    private String timeSlot;
    private Double consultationFee;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    private String status;
}