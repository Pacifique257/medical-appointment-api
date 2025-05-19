package com.example.medical_appointment.service;


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


@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              AvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
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
        }

        appointment.setStatus("CONFIRMED");
        return appointmentRepository.save(appointment);
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
    }
}