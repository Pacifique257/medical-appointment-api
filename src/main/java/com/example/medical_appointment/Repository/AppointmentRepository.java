package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.Appointment;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

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
        String doctorEmail, List<String> statuses
    );

    long countByPatientEmailAndStatusIn(String patientEmail, List<String> statuses);

    List<Appointment> findTop5ByPatientEmailAndStatusInOrderByAppointmentDateAscTimeSlotAsc(
        String patientEmail, List<String> statuses
    );
}
