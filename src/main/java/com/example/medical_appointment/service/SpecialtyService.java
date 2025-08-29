package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Repository.SpecialtyRepository;
<<<<<<< HEAD
import com.example.medical_appointment.dto.SpecialtyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

<<<<<<< HEAD
    public SpecialtyDTO createSpecialty(SpecialtyDTO specialtyDTO) {
        if (specialtyRepository.existsByName(specialtyDTO.getName())) {
            throw new IllegalArgumentException("Une spécialité avec ce nom existe déjà");
        }
        Specialty specialty = new Specialty();
        specialty.setName(specialtyDTO.getName());
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(savedSpecialty);
    }

    public List<SpecialtyDTO> getAllSpecialties() {
        return specialtyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SpecialtyDTO getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spécialité non trouvée avec l'ID : " + id));
        return mapToDTO(specialty);
    }

    public SpecialtyDTO updateSpecialty(Long id, SpecialtyDTO specialtyDTO) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spécialité non trouvée avec l'ID : " + id));
        if (!specialty.getName().equals(specialtyDTO.getName()) && 
            specialtyRepository.existsByName(specialtyDTO.getName())) {
            throw new IllegalArgumentException("Une spécialité avec ce nom existe déjà");
        }
        specialty.setName(specialtyDTO.getName());
        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(updatedSpecialty);
    }

    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new EntityNotFoundException("Spécialité non trouvée avec l'ID : " + id);
        }
        specialtyRepository.deleteById(id);
    }

    private SpecialtyDTO mapToDTO(Specialty specialty) {
        SpecialtyDTO dto = new SpecialtyDTO();
        dto.setId(specialty.getId());
        dto.setName(specialty.getName());
        return dto;
    }
}
=======
    public Specialty createSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }
}
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
