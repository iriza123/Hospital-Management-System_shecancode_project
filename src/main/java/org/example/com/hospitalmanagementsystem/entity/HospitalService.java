package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospital_services",
        uniqueConstraints = @UniqueConstraint(columnNames = {"service_name", "hospital_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();
}
