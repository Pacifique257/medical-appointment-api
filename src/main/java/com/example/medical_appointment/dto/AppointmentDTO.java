package com.example.medical_appointment.dto;



import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long availabilityId;
    private LocalDate appointmentDate;
    private String day;
    private String reason;
    private String timeSlot;
    private Double consultationFee;
    private String status;
}