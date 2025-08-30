package com.example.medical_appointment.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "availabilities")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;
    @OneToOne
    @JoinColumn(name = "appointment_id") // Column in availabilities table
    private Appointment appointment; // Null if slot is free

    public Availability(Long id, User doctor, LocalDate date, String dayOfWeek, String timeSlot, Appointment appointment) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.appointment = appointment;
    
    }

    public Availability() {
    }
    
}