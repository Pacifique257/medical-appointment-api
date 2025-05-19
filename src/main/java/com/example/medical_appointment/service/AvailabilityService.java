package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public Availability createAvailability(Availability availability) {
        if (!"DOCTOR".equals(availability.getDoctor().getRole())) {
            throw new IllegalArgumentException("Only doctors can create availabilities");
        }
        return availabilityRepository.save(availability);
    }

    public List<Availability> getAvailabilitiesByDoctorAndDate(User doctor, LocalDate date) {
        return availabilityRepository.findByDoctorAndDate(doctor, date);
    }

    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
}
