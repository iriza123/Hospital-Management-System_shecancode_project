package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalRegistrationRequest {

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Telephone is required")
    private String telephone;

    @NotBlank(message = "Physical address is required")
    private String physicalAddress;

    @NotNull(message = "Administrator information is required")
    @Valid
    private AdminRegistrationRequest admin;
}
