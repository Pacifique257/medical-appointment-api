package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.securityrateLimited()
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<Availability> createAvailability(@RequestBody AvailabilityDTO availabilityDTO,
                                                          @AuthenticationPrincipal User doctor) {
        Availability availability = new Availability();
        availability.setDoctor(doctor);
        availability.setDate(availabilityDTO.getDate());
        availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
        availability.setTimeSlot(availabilityDTO.getTimeSlot());
        return ResponseEntity.ok(availabilityService.createAvailability(availability));
    }

    @GetMapping("/doctor/{date}")
    public ResponseEntity<List<Availability>> getAvailabilities(@PathVariable String date,
                                                               @AuthenticationPrincipal User doctor) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesByDoctorAndDate(doctor, LocalDate.parse(date)));
    }
}
