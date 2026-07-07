package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponse implements Serializable {
    private Long id;
    private String nationalId;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String role;
    private LocalDateTime createdAt;
}
