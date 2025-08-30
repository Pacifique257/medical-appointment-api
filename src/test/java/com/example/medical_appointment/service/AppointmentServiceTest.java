// package com.example.medical_appointment.service;

// import com.example.medical_appointment.Models.Appointment;
// import com.example.medical_appointment.Models.Availability;
// import com.example.medical_appointment.Models.User;
// import com.example.medical_appointment.Repository.AppointmentRepository;
// import com.example.medical_appointment.Repository.AvailabilityRepository;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// public class AppointmentServiceTest {

//     @Mock
//     private AppointmentRepository appointmentRepository;

//     @Mock
//     private AvailabilityRepository availabilityRepository;

//     @Mock
//     private EmailService emailService;

//     @InjectMocks
//     private AppointmentService appointmentService;

//     @Test
//     public void testCreateAppointment() {
//         User patient = new User();
//         patient.setRole("PATIENT");
//         User doctor = new User();
//         doctor.setRole("DOCTOR");
//         doctor.setConsultationFee(100.0);
//         Availability availability = new Availability();
//         availability.setDoctor(doctor);
//         Appointment appointment = new Appointment();
//         appointment.setPatient(patient);

//         when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));
//         when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

//         Appointment result = appointmentService.createAppointment(appointment, 1L);
//         assertEquals("PENDING", result.getStatus());
//         verify(availabilityRepository, times(1)).deleteById(1L);
//     }
// }