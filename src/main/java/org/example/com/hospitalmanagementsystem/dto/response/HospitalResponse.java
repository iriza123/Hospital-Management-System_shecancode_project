package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HospitalResponse {
    private Long id;
    private String hospitalName;
    private String telephone;
    private String physicalAddress;
    private LocalDateTime createdAt;
    private AdminResponse admin;
}
