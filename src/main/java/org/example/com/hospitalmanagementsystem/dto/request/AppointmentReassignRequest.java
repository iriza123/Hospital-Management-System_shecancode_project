package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentReassignRequest {

    @NotNull(message = "New doctor ID is required")
    private Long newDoctorId;
}
