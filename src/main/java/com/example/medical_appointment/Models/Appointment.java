package com.example.medical_appointment.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @OneToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;

    private LocalDate appointmentDate;

    // Add day field to match the database column
    @Column(name = "day", nullable = false)
    private String day;

    private String timeSlot;
    private String reason;
    private Double consultationFee;
   @Column(nullable = false)
   private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    public Appointment(Long id, User patient, User doctor, Availability availability, LocalDate appointmentDate, String day, String timeSlot, String reason, Double consultationFee, String status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.availability = availability;
        this.appointmentDate = appointmentDate;
        this.day = day;
        this.timeSlot = timeSlot;
        this.reason = reason;
        this.consultationFee = consultationFee;
        this.status = status;
    }

    public Appointment() {
    }

}