package com.example.medical_appointment.dto;


import com.example.medical_appointment.Models.Appointment;
import com.example.medical_appointment.Models.User;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {
    private User user;
    private List<Appointment> appointments;
}