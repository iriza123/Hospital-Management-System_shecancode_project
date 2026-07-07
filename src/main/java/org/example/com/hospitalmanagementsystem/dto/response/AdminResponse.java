package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
}
