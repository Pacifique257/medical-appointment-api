package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorAndDate(User doctor, LocalDate date);
}
