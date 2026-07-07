package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String symptoms;
    private AppointmentStatus status;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long serviceId;
    private String serviceName;
    private DiagnosisResponse diagnosis;
    private LocalDateTime createdAt;
}
