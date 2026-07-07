package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.time.LocalDate;

@Data
public class PatientUpdateRequest {

    private String nationalId;
    private String fullName;

    @Past(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private String address;
    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Emergency contact phone must be valid")
    private String emergencyContactPhone;
}
