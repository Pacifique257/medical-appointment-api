package com.example.medical_appointment.dto;



import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long patientId;
    private Long doctorId;
    private Long availabilityId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String reason;
    private Double consultationFee;

    // Getters and setters
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(Long availabilityId) { this.availabilityId = availabilityId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    @Override
    public String toString() {
        return "AppointmentDTO{patientId=" + patientId + ", doctorId=" + doctorId + 
               ", availabilityId=" + availabilityId + ", appointmentDate=" + appointmentDate + 
               ", timeSlot='" + timeSlot + "', reason='" + reason + "', consultationFee=" + consultationFee + "}";
    }
}