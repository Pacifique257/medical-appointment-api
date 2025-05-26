package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorAndDate(User doctor, LocalDate date);
    List<Availability> findByDoctor(User doctor);
    List<Availability> findByDoctorIdAndDateAndAppointmentIsNull(Long doctorId, LocalDate date);
    List<Availability> findByDoctorIdAndDateAfterAndAppointmentIsNull(Long doctorId, LocalDate date);
    Optional<Availability> findByDoctorIdAndDateAndTimeSlotAndAppointmentIsNull(Long doctorId, LocalDate date, String timeSlot);

}