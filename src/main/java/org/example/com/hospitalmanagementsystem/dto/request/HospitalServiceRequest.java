package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HospitalServiceRequest {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String description;
}
