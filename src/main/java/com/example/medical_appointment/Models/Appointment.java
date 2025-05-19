package com.example.medical_appointment.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

//@Data
//@Entity
//@Table(name = "appointments")
//public class Appointment {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "patient_id", nullable = false)
//    private User patient;
//
//    @ManyToOne
//    @JoinColumn(name = "doctor_id", nullable = false)
//    private User doctor;
//
//    @Column(name = "appointment_date", nullable = false)
//    private LocalDate appointmentDate;
//
//    @Column(name = "day", nullable = false)
//    private String day;
//
//    @Column(name = "reason")
//    private String reason;
//
//    @Column(name = "time_slot", nullable = false)
//    private String timeSlot;
//
//    @Column(name = "consultation_fee", nullable = false)
//    private Double consultationFee;
//
//    @Column(name = "status", nullable = false)
//    private String status;
//}


@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "reason")
    private String reason;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(name = "consultation_fee", nullable = false)
    private Double consultationFee;

    @Column(name = "status", nullable = false)
    private String status;
}