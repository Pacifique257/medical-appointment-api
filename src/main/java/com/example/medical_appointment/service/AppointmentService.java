package com.example.medical_appointment.service;


import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.Availability;
import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.AppointmentRepository;
import com.example.medical_appointment.Repository.AvailabilityRepository;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import com.example.medical_appointment.Repository.UserRepository;
import com.example.medical_appointment.dto.AppointmentDTO;
import com.example.medical_appointment.dto.AvailabilityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    public List<User> getDoctorsBySpecialty(Long specialtyId) {
        return userRepository.findByRoleAndSpecialtyId("DOCTOR", specialtyId);
    }
    
    
    public List<Appointment> getAppointmentsByPatientEmail(String email) {
        System.out.println("Fetching appointments for patient email: " + email);
        User patient = userRepository.findByEmail(email);
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            System.out.println("Invalid patient: " + email);
            throw new IllegalArgumentException("Invalid patient");
        }
        List<Appointment> appointments = appointmentRepository.findByPatientId(patient.getId());
        System.out.println("Retrieved " + appointments.size() + " appointments for patient ID: " + patient.getId());
        return appointments;
    }

   public List<LocalDate> getAvailableDates(Long doctorId) {
        LocalDate today = LocalDate.now();
        System.out.println("Fetching available dates for doctorId: " + doctorId + ", after: " + today);
        List<LocalDate> dates = availabilityRepository
                .findByDoctorIdAndDateAfterAndAppointmentIsNull(doctorId, today)
                .stream()
                .map(Availability::getDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Available dates: " + dates);
        return dates;
    }

    public List<String> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        return availabilityRepository.findByDoctorIdAndDateAndAppointmentIsNull(doctorId, date)
                .stream()
                .map(Availability::getTimeSlot)
                .sorted()
                .collect(Collectors.toList());
    }

    public Double getDoctorConsultationFee(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new IllegalArgumentException("User is not a doctor");
        }
        return doctor.getConsultationFee();
    }

    public AvailabilityDTO getAvailability(Long doctorId, LocalDate date, String timeSlot) {
        Availability availability = availabilityRepository
                .findByDoctorIdAndDateAndTimeSlotAndAppointmentIsNull(doctorId, date, timeSlot)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found or already taken"));

        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getId());
        dto.setDate(availability.getDate());
        dto.setDayOfWeek(availability.getDate().getDayOfWeek().toString());
        dto.setTimeSlot(availability.getTimeSlot());
        return dto;
    }

public Appointment createAppointment(AppointmentDTO appointmentDTO, String patientEmail) {
        System.out.println("Creating appointment with DTO: " + appointmentDTO);
        System.out.println("Appointment date from DTO: " + appointmentDTO.getAppointmentDate());

        User patient = userRepository.findByEmail(patientEmail);
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            System.out.println("Invalid patient: " + patientEmail);
            throw new IllegalArgumentException("Invalid patient");
        }

        User doctor = userRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> {
                    System.out.println("Doctor not found: " + appointmentDTO.getDoctorId());
                    return new IllegalArgumentException("Doctor not found");
                });
        if (!"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Selected user is not a doctor: " + appointmentDTO.getDoctorId());
            throw new IllegalArgumentException("Selected user is not a doctor");
        }

        if (appointmentDTO.getAvailabilityId() == null) {
            System.out.println("Time slot not selected: availabilityId is null");
            throw new IllegalArgumentException("Time slot not selected");
        }

        Availability availability = availabilityRepository.findById(appointmentDTO.getAvailabilityId())
                .orElseThrow(() -> {
                    System.out.println("Availability not found: " + appointmentDTO.getAvailabilityId());
                    return new IllegalArgumentException("Availability not found");
                });
        if (availability.getAppointment() != null) {
            System.out.println("Time slot already taken: availabilityId=" + appointmentDTO.getAvailabilityId());
            throw new IllegalArgumentException("Time slot already taken");
        }
        if (!availability.getDoctor().getId().equals(doctor.getId())) {
            System.out.println("Availability does not belong to doctor: availabilityId=" + 
                              appointmentDTO.getAvailabilityId() + ", doctorId=" + doctor.getId());
            throw new IllegalArgumentException("Availability does not belong to selected doctor");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAvailability(availability);
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setTimeSlot(appointmentDTO.getTimeSlot());
        appointment.setReason(appointmentDTO.getReason());
        appointment.setConsultationFee(appointmentDTO.getConsultationFee());
        appointment.setStatus("PENDING");

        // Set day based on appointmentDate
        LocalDate appointmentDate = appointmentDTO.getAppointmentDate();
        if (appointmentDate == null) {
            System.out.println("Error: appointmentDate is null in DTO");
            throw new IllegalArgumentException("Appointment date is required");
        }
        String day = appointmentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        appointment.setDay(day);
        System.out.println("Setting day: " + day + " for appointmentDate: " + appointmentDate);

        System.out.println("Saving appointment for availabilityId: " + appointmentDTO.getAvailabilityId());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        availability.setAppointment(savedAppointment);
        availabilityRepository.save(availability);

        System.out.println("Appointment created: ID=" + savedAppointment.getId());
        return savedAppointment;
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (!"PATIENT".equals(patient.getRole())) {
            throw new IllegalArgumentException("User is not a patient");
        }
        return appointmentRepository.findByPatientId(patientId);
    }

   public List<Appointment> getDoctorAppointments(Long doctorId) {
    User doctor = userRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    if (!"DOCTOR".equals(doctor.getRole())) {
        throw new IllegalArgumentException("User is not a doctor");
    }
    return appointmentRepository.findByDoctorId(doctorId);
}
   
   public void cancelAppointment(Long appointmentId, String patientEmail) {
        System.out.println("Attempting to cancel appointment ID: " + appointmentId + " by patient: " + patientEmail);

        User patient = userRepository.findByEmail(patientEmail);
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            System.out.println("Invalid patient: " + patientEmail);
            throw new IllegalArgumentException("Invalid patient");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    System.out.println("Appointment not found: " + appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            System.out.println("Patient " + patientEmail + " is not authorized to cancel appointment ID: " + appointmentId);
            throw new IllegalArgumentException("Not authorized to cancel this appointment");
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            System.out.println("Cannot cancel appointment ID: " + appointmentId + ", status: " + appointment.getStatus());
            throw new IllegalArgumentException("Only pending appointments can be cancelled");
        }

        appointment.setStatus("CANCELLED");
        Availability availability = appointment.getAvailability();
        if (availability != null) {
            availability.setAppointment(null);
            availabilityRepository.save(availability);
        }
        appointmentRepository.save(appointment);
        System.out.println("Appointment ID: " + appointmentId + " cancelled successfully");
    }

    public void confirmAppointment(Long appointmentId, String doctorEmail) {
        System.out.println("Attempting to confirm appointment ID: " + appointmentId + " by doctor: " + doctorEmail);

        User doctor = userRepository.findByEmail(doctorEmail);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Invalid doctor: " + doctorEmail);
            throw new IllegalArgumentException("Invalid doctor");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    System.out.println("Appointment not found: " + appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            System.out.println("Doctor " + doctorEmail + " is not authorized to confirm appointment ID: " + appointmentId);
            throw new IllegalArgumentException("Not authorized to confirm this appointment");
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            System.out.println("Cannot confirm appointment ID: " + appointmentId + ", status: " + appointment.getStatus());
            throw new IllegalArgumentException("Only pending appointments can be confirmed");
        }

        appointment.setStatus("CONFIRMED");
        appointmentRepository.save(appointment);
        System.out.println("Appointment ID: " + appointmentId + " confirmed successfully");
    }

    public void completeAppointment(Long appointmentId, String doctorEmail) {
        System.out.println("Attempting to complete appointment ID: " + appointmentId + " by doctor: " + doctorEmail);

        User doctor = userRepository.findByEmail(doctorEmail);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Invalid doctor: " + doctorEmail);
            throw new IllegalArgumentException("Invalid doctor");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    System.out.println("Appointment not found: " + appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            System.out.println("Doctor " + doctorEmail + " is not authorized to complete appointment ID: " + appointmentId);
            throw new IllegalArgumentException("Not authorized to complete this appointment");
        }

        if (!"CONFIRMED".equals(appointment.getStatus())) {
            System.out.println("Cannot complete appointment ID: " + appointmentId + ", status: " + appointment.getStatus());
            throw new IllegalArgumentException("Only confirmed appointments can be completed");
        }

        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);
        System.out.println("Appointment ID: " + appointmentId + " completed successfully");
    }

    public List<Appointment> getAppointmentsByDoctorEmail(String email) {
        System.out.println("Fetching appointments for doctor email: " + email);
        User doctor = userRepository.findByEmail(email);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            System.out.println("Invalid doctor: " + email);
            throw new IllegalArgumentException("Invalid doctor");
        }
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctor.getId());
        System.out.println("Retrieved " + appointments.size() + " appointments for doctor ID: " + doctor.getId());
        return appointments;
    }
    
    // Compter les rendez-vous à venir d'un docteur (PENDING ou CONFIRMED)
    public long countUpcomingDoctorAppointments(String doctorEmail) {
        return appointmentRepository.findByDoctorEmailAndStatusInAndAppointmentDateGreaterThanEqual(
                doctorEmail, List.of("PENDING", "CONFIRMED"), LocalDate.now()).size();
    }

    // Compter les rendez-vous à venir d'un patient (PENDING ou CONFIRMED)
    public long countUpcomingPatientAppointments(String patientEmail) {
        return appointmentRepository.findByPatientEmailAndStatusInAndAppointmentDateGreaterThanEqual(
                patientEmail, List.of("PENDING", "CONFIRMED"), LocalDate.now()).size();
    }

    // Récupérer les 5 prochains rendez-vous d'un docteur
    public List<Appointment> getNextDoctorAppointments(String doctorEmail) {
        return appointmentRepository.findTop5ByDoctorEmailAndStatusInOrderByAppointmentDateAscTimeSlotAsc(
            doctorEmail,
            List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED")
        );
    }

    // Récupérer les 5 prochains rendez-vous d'un patient
    public List<Appointment> getNextPatientAppointments(String patientEmail) {
        return appointmentRepository.findTop5ByPatientEmailAndStatusInOrderByAppointmentDateAscTimeSlotAsc(
            patientEmail,
            List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED")
        );
    }
    public long countDoctorAppointments(String doctorEmail) {
        return appointmentRepository.countByDoctorEmailAndStatusIn(
            doctorEmail,
            List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED")
        );
    }
    public long countPatientAppointments(String patientEmail) {
        return appointmentRepository.countByPatientEmailAndStatusIn(
            patientEmail,
            List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED")
        );
    }
    
}