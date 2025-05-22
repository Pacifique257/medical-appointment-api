package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;




@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public void createAvailability(Availability availability) {
        try {
            availabilityRepository.save(availability);
            System.out.println("Saved availability: doctor=" + availability.getDoctor().getEmail() +
                    ", date=" + availability.getDate() +
                    ", timeSlot=" + availability.getTimeSlot());
        } catch (Exception e) {
            System.out.println("Error saving availability: " + e.getMessage());
            throw new RuntimeException("Failed to save availability", e);
        }
    }

    public List<Availability> getAvailabilitiesByDoctor(User doctor) {
        try {
            List<Availability> availabilities = availabilityRepository.findByDoctor(doctor);
            System.out.println("Retrieved " + availabilities.size() + " availabilities for doctor: " + doctor.getEmail());
            return availabilities;
        } catch (Exception e) {
            System.out.println("Error retrieving availabilities: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve availabilities", e);
        }
    }
} 
