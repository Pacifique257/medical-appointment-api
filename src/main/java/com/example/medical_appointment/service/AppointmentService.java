package com.example.medical_appointment.service;

<<<<<<< HEAD
import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.dto.AvailabilityDTO;
import com.example.medical_appointment.Repository.AppointmentRepository;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import com.example.medical_appointment.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
=======

import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AppointmentRepository;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

//@Service
//public class AppointmentService {
//
//    private final AppointmentRepository appointmentRepository;
//    private final AvailabilityRepository availabilityRepository;
//    private final EmailService emailService;
//
//    @Autowired
//    public AppointmentService(AppointmentRepository appointmentRepository,
//                              AvailabilityRepository availabilityRepository,
//                              EmailService emailService) {
//        this.appointmentRepository = appointmentRepository;
//        this.availabilityRepository = availabilityRepository;
//        this.emailService = emailService;
//    }
//
//    @Transactional
//    public Appointment createAppointment(Appointment appointment, Long availabilityId) {
//        Availability availability = availabilityRepository.findById(availabilityId)
//                .orElseThrow(() -> new RuntimeException("Availability not found"));
//        
//        if (!"PATIENT".equals(appointment.getPatient().getRole())) {
//            throw new IllegalArgumentException("Only patients can create appointments");
//        }
//
//        appointment.setDoctor(availability.getDoctor());
//        appointment.setAppointmentDate(availability.getDate());
//        appointment.setDay(availability.getDayOfWeek());
//        appointment.setTimeSlot(availability.getTimeSlot());
//        appointment.setConsultationFee(availability.getDoctor().getConsultationFee());
//        appointment.setStatus("PENDING");
//
//        Appointment savedAppointment = appointmentRepository.save(appointment);
//        availabilityRepository.deleteById(availabilityId); // Remove the booked slot
//
//        return savedAppointment;
//    }
//
//    @Transactional
//    public Appointment confirmAppointment(Long id) {
//        Appointment appointment = appointmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//        
//        if (!"PENDING".equals(appointment.getStatus())) {
//            throw new IllegalStateException("Only pending appointments can be confirmed");
//        }
//
//        appointment.setStatus("CONFIRMED");
//        Appointment updatedAppointment = appointmentRepository.save(appointment);
//        
//        emailService.sendConfirmationEmail(appointment.getPatient().getEmail(),
//                "Appointment Confirmed",
//                "Your appointment with Dr. " + appointment.getDoctor().getLastName() +
//                        " on " + appointment.getAppointmentDate() + " at " + appointment.getTimeSlot() +
//                        " has been confirmed.");
//
//        return updatedAppointment;
//    }
//
//    @Transactional
//    public Appointment cancelAppointment(Long id, User user) {
//        Appointment appointment = appointmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//        
//        if (!appointment.getPatient().equals(user) && !"DOCTOR".equals(user.getRole())) {
//            throw new IllegalArgumentException("Only the patient or a doctor can cancel an appointment");
//        }
//
//        if ("COMPLETED".equals(appointment.getStatus())) {
//            throw new IllegalStateException("Completed appointments cannot be cancelled");
//        }
//
//        appointment.setStatus("CANCELLED");
//        return appointmentRepository.save(appointment);
//    }
//
//    @Transactional
//    public Appointment completeAppointment(Long id) {
//        Appointment appointment = appointmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//        
//        if (!"CONFIRMED".equals(appointment.getStatus())) {
//            throw new IllegalStateException("Only confirmed appointments can be completed");
//        }
//
//        appointment.setStatus("COMPLETED");
//        return appointmentRepository.save(appointment);
//    }
//
//    public List<Appointment> getPatientAppointments(User patient) {
//        return appointmentRepository.findByPatient(patient);
//    }
//
//    public List<Appointment> getDoctorAppointments(User doctor, LocalDate date) {
//        return appointmentRepository.findByDoctorAndDate(doctor, date);
//    }
//    
//}



>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)

@Service
public class AppointmentService {

<<<<<<< HEAD
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, 
                              AvailabilityRepository availabilityRepository, 
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO, String patientEmail) {
        logger.info("Création d'un rendez-vous pour patientEmail: {}, appointmentDTO: {}", patientEmail, appointmentDTO);

        // Vérifier que l'utilisateur est un PATIENT
        User patient = userRepository.findByEmail(patientEmail);
        if (patient == null) {
            logger.error("Patient non trouvé avec l'email : {}", patientEmail);
            throw new EntityNotFoundException("Patient non trouvé avec l'email : " + patientEmail);
        }
        if (!"PATIENT".equals(patient.getRole())) {
            logger.error("L'utilisateur avec l'email {} n'est pas un PATIENT, rôle: {}", patientEmail, patient.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle PATIENT");
        }

        // Vérifier que le docteur existe et a le rôle DOCTOR
        User doctor = userRepository.findUserById(appointmentDTO.getDoctorId());
        if (doctor == null) {
            logger.error("Docteur non trouvé avec l'ID : {}", appointmentDTO.getDoctorId());
            throw new EntityNotFoundException("Docteur non trouvé avec l'ID : " + appointmentDTO.getDoctorId());
        }
        if (!"DOCTOR".equals(doctor.getRole())) {
            logger.error("L'utilisateur avec l'ID {} n'est pas un DOCTOR, rôle: {}", appointmentDTO.getDoctorId(), doctor.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle DOCTOR");
        }

        // Vérifier que la disponibilité existe et est libre
        Availability availability = availabilityRepository.findById(appointmentDTO.getAvailabilityId())
                .orElseThrow(() -> new EntityNotFoundException("Disponibilité non trouvée avec l'ID : " + appointmentDTO.getAvailabilityId()));
        if (availability.getAppointment() != null) {
            logger.error("La disponibilité avec l'ID {} est déjà prise", appointmentDTO.getAvailabilityId());
            throw new IllegalStateException("La disponibilité est déjà associée à un rendez-vous");
        }

        // Vérifier que appointmentDate, day et timeSlot correspondent à la disponibilité
        if (!availability.getDate().equals(appointmentDTO.getAppointmentDate()) ||
            !availability.getDayOfWeek().equalsIgnoreCase(appointmentDTO.getDay()) ||
            !availability.getTimeSlot().equals(appointmentDTO.getTimeSlot())) {
            logger.error("Les données du rendez-vous ne correspondent pas à la disponibilité ID: {}", appointmentDTO.getAvailabilityId());
            throw new IllegalArgumentException("Les données du rendez-vous doivent correspondre à la disponibilité");
        }

        // Valider le format du timeSlot
        validateTimeSlot(appointmentDTO.getTimeSlot());

        // Valider la raison
        if (appointmentDTO.getReason() == null || appointmentDTO.getReason().trim().isEmpty()) {
            logger.error("La raison du rendez-vous est vide");
            throw new IllegalArgumentException("La raison du rendez-vous est requise");
        }

        // Créer le rendez-vous
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAvailability(availability);
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setDay(appointmentDTO.getDay());
        appointment.setTimeSlot(appointmentDTO.getTimeSlot());
        appointment.setReason(appointmentDTO.getReason());
        appointment.setConsultationFee(appointmentDTO.getConsultationFee());
        appointment.setStatus("PENDING");

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Rendez-vous créé avec succès, ID: {}", savedAppointment.getId());
        return mapToDTO(savedAppointment);
    }

    public List<AppointmentDTO> getAllAppointments() {
        logger.info("Récupération de tous les rendez-vous");
        return appointmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(Long id) {
        logger.info("Récupération du rendez-vous avec l'ID: {}", id);
        Appointment appointment = appointmentRepository.findAppointmentById(id);
        if (appointment == null) {
            logger.error("Rendez-vous non trouvé avec l'ID : {}", id);
            throw new EntityNotFoundException("Rendez-vous non trouvé avec l'ID : " + id);
        }
        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByPatient(String patientEmail) {
        logger.info("Récupération des rendez-vous pour patientEmail: {}", patientEmail);
        User patient = userRepository.findByEmail(patientEmail);
        if (patient == null) {
            logger.error("Patient non trouvé avec l'email : {}", patientEmail);
            throw new EntityNotFoundException("Patient non trouvé avec l'email : " + patientEmail);
        }
        return appointmentRepository.findByPatientId(patient.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByDoctor(String doctorEmail) {
        logger.info("Récupération des rendez-vous pour doctorEmail: {}", doctorEmail);
        User doctor = userRepository.findByEmail(doctorEmail);
        if (doctor == null) {
            logger.error("Docteur non trouvé avec l'email : {}", doctorEmail);
            throw new EntityNotFoundException("Docteur non trouvé avec l'email : " + doctorEmail);
        }
        return appointmentRepository.findByDoctorId(doctor.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO confirmAppointment(Long id, String adminEmail) {
        logger.info("Confirmation du rendez-vous ID: {} par adminEmail: {}", id, adminEmail);
        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            logger.error("Admin non trouvé avec l'email : {}", adminEmail);
            throw new EntityNotFoundException("Admin non trouvé avec l'email : " + adminEmail);
        }
        if (!"ADMIN".equals(admin.getRole())) {
            logger.error("L'utilisateur avec l'email {} n'est pas un ADMIN, rôle: {}", adminEmail, admin.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle ADMIN");
        }

        Appointment appointment = appointmentRepository.findAppointmentById(id);
        if (appointment == null) {
            logger.error("Rendez-vous non trouvé avec l'ID : {}", id);
            throw new EntityNotFoundException("Rendez-vous non trouvé avec l'ID : " + id);
        }
        if (!"PENDING".equals(appointment.getStatus())) {
            logger.error("Le rendez-vous ID: {} n'est pas en attente, statut: {}", id, appointment.getStatus());
            throw new IllegalStateException("Seuls les rendez-vous en attente peuvent être confirmés");
=======
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final EmailService emailService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              AvailabilityRepository availabilityRepository,
                              EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Appointment createAppointment(Appointment appointment, Long availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        
        if (!"PATIENT".equals(appointment.getPatient().getRole())) {
            throw new IllegalArgumentException("Only patients can create appointments");
        }

        appointment.setDoctor(availability.getDoctor());
        appointment.setAppointmentDate(availability.getDate());
        appointment.setDay(availability.getDayOfWeek());
        appointment.setTimeSlot(availability.getTimeSlot());
        appointment.setConsultationFee(availability.getDoctor().getConsultationFee());
        appointment.setStatus("PENDING");

        Appointment savedAppointment = appointmentRepository.save(appointment);
        availabilityRepository.deleteById(availabilityId);

        return savedAppointment;
    }

    @Transactional
    public Appointment confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!"PENDING".equals(appointment.getStatus())) {
            throw new IllegalStateException("Only pending appointments can be confirmed");
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
        }

        appointment.setStatus("CONFIRMED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
<<<<<<< HEAD
        logger.info("Rendez-vous ID: {} confirmé avec succès", id);
        return mapToDTO(updatedAppointment);
    }

    public AppointmentDTO cancelAppointment(Long id, String adminEmail) {
        logger.info("Annulation du rendez-vous ID: {} par adminEmail: {}", id, adminEmail);
        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            logger.error("Admin non trouvé avec l'email : {}", adminEmail);
            throw new EntityNotFoundException("Admin non trouvé avec l'email : " + adminEmail);
        }
        if (!"ADMIN".equals(admin.getRole())) {
            logger.error("L'utilisateur avec l'email {} n'est pas un ADMIN, rôle: {}", adminEmail, admin.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle ADMIN");
        }

        Appointment appointment = appointmentRepository.findAppointmentById(id);
        if (appointment == null) {
            logger.error("Rendez-vous non trouvé avec l'ID : {}", id);
            throw new EntityNotFoundException("Rendez-vous non trouvé avec l'ID : " + id);
        }
        if ("CANCELLED".equals(appointment.getStatus()) || "COMPLETED".equals(appointment.getStatus())) {
            logger.error("Le rendez-vous ID: {} est déjà annulé ou terminé, statut: {}", id, appointment.getStatus());
            throw new IllegalStateException("Le rendez-vous est déjà annulé ou terminé");
        }

        appointment.setStatus("CANCELLED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Rendez-vous ID: {} annulé avec succès", id);
        return mapToDTO(updatedAppointment);
    }

    public AppointmentDTO completeAppointment(Long id, String adminEmail) {
        logger.info("Achèvement du rendez-vous ID: {} par adminEmail: {}", id, adminEmail);
        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            logger.error("Admin non trouvé avec l'email : {}", adminEmail);
            throw new EntityNotFoundException("Admin non trouvé avec l'email : " + adminEmail);
        }
        if (!"ADMIN".equals(admin.getRole())) {
            logger.error("L'utilisateur avec l'email {} n'est pas un ADMIN, rôle: {}", adminEmail, admin.getRole());
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle ADMIN");
        }

        Appointment appointment = appointmentRepository.findAppointmentById(id);
        if (appointment == null) {
            logger.error("Rendez-vous non trouvé avec l'ID : {}", id);
            throw new EntityNotFoundException("Rendez-vous non trouvé avec l'ID : " + id);
        }
        if ("CANCELLED".equals(appointment.getStatus()) || "COMPLETED".equals(appointment.getStatus())) {
            logger.error("Le rendez-vous ID: {} est déjà annulé ou terminé, statut: {}", id, appointment.getStatus());
            throw new IllegalStateException("Le rendez-vous est déjà annulé ou terminé");
        }

        appointment.setStatus("COMPLETED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Rendez-vous ID: {} terminé avec succès", id);
        return mapToDTO(updatedAppointment);
    }

    public List<User> getDoctorsBySpecialty(Long specialtyId) {
        logger.info("Récupération des docteurs pour specialtyId: {}", specialtyId);
        List<User> doctors = userRepository.findByRole("DOCTOR");
        if (doctors.isEmpty()) {
            logger.warn("Aucun docteur trouvé avec le rôle DOCTOR");
        }
        return doctors.stream()
                .filter(user -> user.getSpecialty() != null && user.getSpecialty().getId().equals(specialtyId))
                .collect(Collectors.toList());
    }

    public List<LocalDate> getAvailableDates(Long doctorId) {
        logger.info("Récupération des dates disponibles pour doctorId: {}", doctorId);
        return availabilityRepository.findByDoctorIdAndDateAfterAndAppointmentIsNull(doctorId, LocalDate.now())
                .stream()
                .map(Availability::getDate)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        logger.info("Récupération des créneaux disponibles pour doctorId: {} et date: {}", doctorId, date);
        return availabilityRepository.findByDoctorIdAndDateAndAppointmentIsNull(doctorId, date)
                .stream()
                .map(Availability::getTimeSlot)
                .collect(Collectors.toList());
    }

    public Double getDoctorConsultationFee(Long doctorId) {
        logger.info("Récupération des frais de consultation pour doctorId: {}", doctorId);
        User doctor = userRepository.findUserById(doctorId);
        if (doctor == null) {
            logger.error("Docteur non trouvé avec l'ID : {}", doctorId);
            throw new EntityNotFoundException("Docteur non trouvé avec l'ID : " + doctorId);
        }
        return doctor.getConsultationFee() != null ? doctor.getConsultationFee() : 0.0;
    }

    public AvailabilityDTO getAvailability(Long doctorId, LocalDate date, String timeSlot) {
        logger.info("Récupération de la disponibilité pour doctorId: {}, date: {}, timeSlot: {}", doctorId, date, timeSlot);
        Optional<Availability> availabilityOptional = availabilityRepository.findByDoctorIdAndDateAndTimeSlotAndAppointmentIsNull(doctorId, date, timeSlot);
        Availability availability = availabilityOptional.orElseThrow(() -> {
            logger.error("Disponibilité non trouvée pour doctorId: {}, date: {}, timeSlot: {}", doctorId, date, timeSlot);
            return new EntityNotFoundException("Disponibilité non trouvée pour doctorId: " + doctorId + ", date: " + date + ", timeSlot: " + timeSlot);
        });
        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getId());
        dto.setDate(availability.getDate());
        dto.setDayOfWeek(availability.getDayOfWeek());
        dto.setTimeSlot(availability.getTimeSlot());
        return dto;
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

    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setAvailabilityId(appointment.getAvailability().getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setDay(appointment.getDay());
        dto.setTimeSlot(appointment.getTimeSlot());
        dto.setReason(appointment.getReason());
        dto.setConsultationFee(appointment.getConsultationFee());
        dto.setStatus(appointment.getStatus());
        return dto;
=======
        
        emailService.sendConfirmationEmail(appointment.getPatient().getEmail(),
                "Appointment Confirmed",
                "Your appointment with Dr. " + appointment.getDoctor().getLastName() +
                        " on " + appointment.getAppointmentDate() + " at " + appointment.getTimeSlot() +
                        " has been confirmed.");

        return updatedAppointment;
    }

    @Transactional
    public Appointment cancelAppointment(Long id, User user) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!appointment.getPatient().equals(user) && !"DOCTOR".equals(user.getRole())) {
            throw new IllegalArgumentException("Only the patient or a doctor can cancel an appointment");
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Completed appointments cannot be cancelled");
        }

        appointment.setStatus("CANCELLED");
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Only confirmed appointments can be completed");
        }

        appointment.setStatus("COMPLETED");
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(User patient) {
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getDoctorAppointments(User doctor, LocalDate appointmentDate) {
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, appointmentDate);
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    }
}