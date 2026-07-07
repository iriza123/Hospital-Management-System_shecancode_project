package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {

    private String nationalId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
    private String phoneNumber;

    private String address;

    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Emergency contact phone must be valid")
    private String emergencyContactPhone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
