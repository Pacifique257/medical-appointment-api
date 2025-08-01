package com.example.medical_appointment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Medical Appointment API", version = "v1", description = "API pour gérer les utilisateurs et rendez-vous médicaux"))
public class SwaggerConfig {
}