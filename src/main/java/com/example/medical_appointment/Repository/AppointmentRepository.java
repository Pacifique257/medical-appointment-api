package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctorAndAppointmentDate(User doctor, LocalDate appointmentDate);
}