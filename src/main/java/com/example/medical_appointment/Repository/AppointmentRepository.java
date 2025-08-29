package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.Appointment;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorEmailAndStatusInAndAppointmentDateGreaterThanEqual(
            String doctorEmail, List<String> statuses, LocalDate date);
    List<Appointment> findByPatientEmailAndStatusInAndAppointmentDateGreaterThanEqual(
            String patientEmail, List<String> statuses, LocalDate date);
    long countByDoctorEmailAndStatusIn(String doctorEmail, List<String> statuses);
    List<Appointment> findTop5ByDoctorEmailAndStatusInOrderByAppointmentDateAscTimeSlotAsc(
            String doctorEmail, List<String> statuses);
    long countByPatientEmailAndStatusIn(String patientEmail, List<String> statuses);
    List<Appointment> findTop5ByPatientEmailAndStatusInOrderByAppointmentDateAscTimeSlotAsc(
            String patientEmail, List<String> statuses);
    
    @Query("SELECT a FROM Appointment a WHERE a.availability.id = :availabilityId")
    Appointment findByAvailabilityId(Long availabilityId);
    
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Appointment findAppointmentById(Long id); // Méthode personnalisée pour éviter Optional<Appointment>
=======
import com.example.medical_appointment.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

//public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
//    List<Appointment> findByPatient(User patient);
//    List<Appointment> findByDoctorAndDate(User doctor, LocalDate date);
//
//    public List<Appointment> findByDoctorAndAppointmentDate(User doctor, LocalDate date);
//}



public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctorAndAppointmentDate(User doctor, LocalDate appointmentDate);
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}