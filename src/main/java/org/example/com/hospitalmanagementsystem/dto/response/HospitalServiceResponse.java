package org.example.com.hospitalmanagementsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class HospitalServiceResponse implements Serializable {
    private Long id;
    private String serviceName;
    private String description;
    private Long hospitalId;
    private String hospitalName;
    private LocalDateTime createdAt;
}
