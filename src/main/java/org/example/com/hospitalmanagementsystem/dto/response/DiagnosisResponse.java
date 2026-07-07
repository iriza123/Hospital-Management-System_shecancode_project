package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DiagnosisResponse {
    private Long id;
    private String symptoms;
    private String diagnosisNotes;
    private String recommendedTreatment;
    private String prescriptionFileName;
    private Long appointmentId;
    private LocalDateTime createdAt;
}
