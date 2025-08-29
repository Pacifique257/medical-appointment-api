package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AvailabilityRepository;
<<<<<<< HEAD
import com.example.medical_appointment.Repository.UserRepository;
import com.example.medical_appointment.dto.AvailabilityDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
<<<<<<< HEAD
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
=======
import java.util.List;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)

@Service
public class AvailabilityService {

<<<<<<< HEAD
    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    public AvailabilityDTO createAvailability(AvailabilityDTO availabilityDTO) {
        logger.info("Création d'une disponibilité pour doctorId: {}, date: {}, timeSlot: {}", 
                availabilityDTO.getDoctorId(), availabilityDTO.getDate(), availabilityDTO.getTimeSlot());

        // Vérifier que l'utilisateur est un DOCTOR
        User doctor = userRepository.findById(availabilityDTO.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Docteur non trouvé avec l'ID : " + availabilityDTO.getDoctorId()));
        if (!"DOCTOR".equals(doctor.getRole())) {
            logger.error("L'utilisateur avec l'ID {} n'est pas un DOCTOR, rôle: {}", availabilityDTO.getDoctorId(), doctor.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle DOCTOR");
        }

        // Valider le format du timeSlot (ex. "09:00-10:00")
        validateTimeSlot(availabilityDTO.getTimeSlot());

        // Vérifier la cohérence de dayOfWeek avec la date
        String expectedDayOfWeek = availabilityDTO.getDate().getDayOfWeek().name();
        if (!availabilityDTO.getDayOfWeek().equalsIgnoreCase(expectedDayOfWeek)) {
            logger.error("Le jour de la semaine {} ne correspond pas à la date {}", 
                    availabilityDTO.getDayOfWeek(), availabilityDTO.getDate());
            throw new IllegalArgumentException("Le jour de la semaine ne correspond pas à la date");
        }

        // Vérifier les chevauchements
        if (availabilityRepository.findByDoctorIdAndDateAndTimeSlotAndAppointmentIsNull(
                availabilityDTO.getDoctorId(), availabilityDTO.getDate(), availabilityDTO.getTimeSlot()).isPresent()) {
            logger.error("Un créneau existe déjà pour le docteur {} à la date {} et au créneau {}", 
                    doctor.getEmail(), availabilityDTO.getDate(), availabilityDTO.getTimeSlot());
            throw new IllegalArgumentException("Un créneau existe déjà pour ce docteur à cette date et heure");
        }

        Availability availability = new Availability();
        availability.setDoctor(doctor);
        availability.setDate(availabilityDTO.getDate());
        availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
        availability.setTimeSlot(availabilityDTO.getTimeSlot());

        Availability savedAvailability = availabilityRepository.save(availability);
        logger.info("Disponibilité créée avec succès pour doctorId: {}, id: {}", 
                availabilityDTO.getDoctorId(), savedAvailability.getId());
        return mapToDTO(savedAvailability);
    }

    public List<AvailabilityDTO> getAllAvailabilities() {
        logger.info("Récupération de toutes les disponibilités");
        return availabilityRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvailabilityDTO getAvailabilityById(Long id) {
        logger.info("Récupération de la disponibilité avec l'ID: {}", id);
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilité non trouvée avec l'ID : " + id));
        return mapToDTO(availability);
    }

    public List<AvailabilityDTO> getAvailabilitiesByDoctor(Long doctorId) {
        logger.info("Récupération des disponibilités pour doctorId: {}", doctorId);
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Docteur non trouvé avec l'ID : " + doctorId));
        return availabilityRepository.findByDoctor(doctor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getAvailabilitiesByDoctorAndDate(Long doctorId, LocalDate date) {
        logger.info("Récupération des disponibilités pour doctorId: {} et date: {}", doctorId, date);
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Docteur non trouvé avec l'ID : " + doctorId));
        return availabilityRepository.findByDoctorAndDate(doctor, date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getAvailableSlotsByDoctorAndDate(Long doctorId, LocalDate date) {
        logger.info("Récupération des créneaux libres pour doctorId: {} et date: {}", doctorId, date);
        return availabilityRepository.findByDoctorIdAndDateAndAppointmentIsNull(doctorId, date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getAvailableSlotsByDoctorAfterDate(Long doctorId, LocalDate date) {
        logger.info("Récupération des créneaux libres pour doctorId: {} après la date: {}", doctorId, date);
        return availabilityRepository.findByDoctorIdAndDateAfterAndAppointmentIsNull(doctorId, date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvailabilityDTO updateAvailability(Long id, AvailabilityDTO availabilityDTO) {
        logger.info("Mise à jour de la disponibilité avec l'ID: {}", id);
        Availability existingAvailability = availabilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilité non trouvée avec l'ID : " + id));

        // Vérifier que l'utilisateur est un DOCTOR
        User doctor = userRepository.findById(availabilityDTO.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Docteur non trouvé avec l'ID : " + availabilityDTO.getDoctorId()));
        if (!"DOCTOR".equals(doctor.getRole())) {
            logger.error("L'utilisateur avec l'ID {} n'est pas un DOCTOR, rôle: {}", availabilityDTO.getDoctorId(), doctor.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle DOCTOR");
        }

        // Valider le format du timeSlot
        validateTimeSlot(availabilityDTO.getTimeSlot());

        // Vérifier la cohérence de dayOfWeek
        String expectedDayOfWeek = availabilityDTO.getDate().getDayOfWeek().name();
        if (!availabilityDTO.getDayOfWeek().equalsIgnoreCase(expectedDayOfWeek)) {
            logger.error("Le jour de la semaine {} ne correspond pas à la date {}", 
                    availabilityDTO.getDayOfWeek(), availabilityDTO.getDate());
            throw new IllegalArgumentException("Le jour de la semaine ne correspond pas à la date");
        }

        // Vérifier les chevauchements (sauf pour la disponibilité actuelle)
        if (!existingAvailability.getTimeSlot().equals(availabilityDTO.getTimeSlot()) || 
            !existingAvailability.getDate().equals(availabilityDTO.getDate()) ||
            !existingAvailability.getDoctor().getId().equals(availabilityDTO.getDoctorId())) {
            if (availabilityRepository.findByDoctorIdAndDateAndTimeSlotAndAppointmentIsNull(
                    availabilityDTO.getDoctorId(), availabilityDTO.getDate(), availabilityDTO.getTimeSlot()).isPresent()) {
                logger.error("Un créneau existe déjà pour le docteur {} à la date {} et au créneau {}", 
                        doctor.getEmail(), availabilityDTO.getDate(), availabilityDTO.getTimeSlot());
                throw new IllegalArgumentException("Un créneau existe déjà pour ce docteur à cette date et heure");
            }
        }

        existingAvailability.setDoctor(doctor);
        existingAvailability.setDate(availabilityDTO.getDate());
        existingAvailability.setDayOfWeek(availabilityDTO.getDayOfWeek());
        existingAvailability.setTimeSlot(availabilityDTO.getTimeSlot());

        Availability updatedAvailability = availabilityRepository.save(existingAvailability);
        logger.info("Disponibilité mise à jour avec succès pour id: {}", id);
        return mapToDTO(updatedAvailability);
    }

    public void deleteAvailability(Long id) {
        logger.info("Suppression de la disponibilité avec l'ID: {}", id);
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilité non trouvée avec l'ID : " + id));
        if (availability.getAppointment() != null) {
            logger.error("La disponibilité avec l'ID {} est associée à un rendez-vous et ne peut pas être supprimée", id);
            throw new IllegalStateException("Impossible de supprimer une disponibilité associée à un rendez-vous");
        }
        availabilityRepository.deleteById(id);
        logger.info("Disponibilité supprimée avec succès pour id: {}", id);
    }

    private void validateTimeSlot(String timeSlot) {
        if (timeSlot == null || !timeSlot.matches("\\d{2}:\\d{2}-\\d{2}:\\d{2}")) {
            logger.error("Format de timeSlot invalide: {}", timeSlot);
            throw new IllegalArgumentException("Le format du créneau horaire doit être HH:mm-HH:mm");
        }
        String[] times = timeSlot.split("-");
        try {
            LocalTime startTime = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm"));
            if (!endTime.isAfter(startTime)) {
                logger.error("L'heure de fin {} n'est pas postérieure à l'heure de début {}", endTime, startTime);
                throw new IllegalArgumentException("L'heure de fin doit être postérieure à l'heure de début");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la validation du timeSlot {}: {}", timeSlot, e.getMessage());
            throw new IllegalArgumentException("Format de créneau horaire invalide: " + e.getMessage());
        }
    }

    private AvailabilityDTO mapToDTO(Availability availability) {
        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getId());
        dto.setDate(availability.getDate());
        dto.setDayOfWeek(availability.getDayOfWeek());
        dto.setTimeSlot(availability.getTimeSlot());
        return dto;
    }
}
=======
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
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
