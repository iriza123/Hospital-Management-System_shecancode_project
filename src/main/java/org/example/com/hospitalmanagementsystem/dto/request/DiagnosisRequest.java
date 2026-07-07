package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiagnosisRequest {

    @NotBlank(message = "Symptoms are required")
    private String symptoms;

    @NotBlank(message = "Diagnosis notes are required")
    private String diagnosisNotes;

    private String recommendedTreatment;
}
