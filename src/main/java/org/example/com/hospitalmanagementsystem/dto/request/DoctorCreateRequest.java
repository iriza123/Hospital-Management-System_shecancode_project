package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.util.List;

@Data
public class DoctorCreateRequest {

    @NotBlank(message = "Medical license number is required")
    private String medicalLicenseNumber;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Specialisation is required")
    private String specialisation;

    @NotEmpty(message = "At least one service must be assigned")
    private List<Long> serviceIds;
}
