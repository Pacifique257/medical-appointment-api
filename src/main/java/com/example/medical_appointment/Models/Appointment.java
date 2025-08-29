package com.example.medical_appointment.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
<<<<<<< HEAD
=======
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

>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)

@Entity
@Table(name = "appointments")
@Data
public class Appointment {
<<<<<<< HEAD
=======

>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
<<<<<<< HEAD
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
=======
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
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}