package com.example.medical_appointment.Repository;


import com.example.medical_appointment.Models.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
