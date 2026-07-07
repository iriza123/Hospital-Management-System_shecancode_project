package org.example.com.hospitalmanagementsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.util.List;

@Data
public class DoctorUpdateRequest {

    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private Gender gender;
    private String specialisation;
    private List<Long> serviceIds;
}
