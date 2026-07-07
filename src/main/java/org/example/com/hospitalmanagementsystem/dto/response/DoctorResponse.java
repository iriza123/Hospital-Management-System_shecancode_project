package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.com.hospitalmanagementsystem.enums.Gender;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DoctorResponse implements Serializable {
    private Long id;
    private String medicalLicenseNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private String specialisation;
    private boolean active;
    private String role;
    private Long hospitalId;
    private String hospitalName;
    private List<HospitalServiceResponse> services;
    private LocalDateTime createdAt;
}
