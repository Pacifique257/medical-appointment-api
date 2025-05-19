package com.example.medical_appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // => fait aussi un @ComponentScan sur ce package et ses sous-packages
public class MedicalAppointmentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalAppointmentApiApplication.class, args);
    }
}
