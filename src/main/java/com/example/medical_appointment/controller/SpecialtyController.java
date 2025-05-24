
package com.example.medical_appointment.controller;



import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyController(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        List<?> specialtyData = specialties.stream()
                .map(specialty -> {
                    java.util.Map<String, Object> data = new java.util.HashMap<>();
                    data.put("id", specialty.getId());
                    data.put("name", specialty.getName());
                    return data;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(specialtyData);
    }
}