package com.example.medical_appointment.dto;



import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityDTO {
    private Long id;
    private Long doctorId;
    private LocalDate date;
    private String dayOfWeek;
    private String timeSlot;
}