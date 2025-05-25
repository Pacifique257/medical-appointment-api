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
import java.util.List;
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

    public List<LocalDate> getAvailableDates(Long doctorId) {
        LocalDate today = LocalDate.now();
        return availabilityRepository.findByDoctorIdAndDateAfterAndAppointmentIsNull(doctorId, today)
                .stream()
                .map(Availability::getDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
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
    User patient = userRepository.findByEmail(patientEmail);
    if (patient == null || !"PATIENT".equals(patient.getRole())) {
        throw new IllegalArgumentException("Invalid patient");
    }

    User doctor = userRepository.findById(appointmentDTO.getDoctorId())
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    if (!"DOCTOR".equals(doctor.getRole())) {
        throw new IllegalArgumentException("Selected user is not a doctor");
    }

    Availability availability = availabilityRepository.findById(appointmentDTO.getAvailabilityId())
            .orElseThrow(() -> new IllegalArgumentException("Availability not found"));
    if (availability.getAppointment() != null) {
        throw new IllegalArgumentException("Time slot already taken");
    }
    if (!availability.getDoctor().getId().equals(doctor.getId())) {
        throw new IllegalArgumentException("Availability does not belong to selected doctor");
    }

    Appointment appointment = new Appointment();
    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setAvailability(availability);
    appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
    appointment.setReason(appointmentDTO.getReason());
    appointment.setTimeSlot(appointmentDTO.getTimeSlot());
    appointment.setConsultationFee(appointmentDTO.getConsultationFee());
    appointment.setStatus("PENDING");

    Appointment savedAppointment = appointmentRepository.save(appointment);
    availability.setAppointment(savedAppointment);
    availabilityRepository.save(availability);

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
}