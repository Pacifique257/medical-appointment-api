package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import com.example.medical_appointment.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
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

    public List<Availability> getAvailabilitiesByDoctor(User doctor) {
        return availabilityRepository.findByDoctor(doctor);
    }

    public Availability getAvailabilityById(Long id) {
        return availabilityRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
}  
