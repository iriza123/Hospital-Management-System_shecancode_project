package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_name", nullable = false, unique = true)
    private String hospitalName;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "physical_address", nullable = false)
    private String physicalAddress;

    @OneToOne(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Admin admin;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HospitalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Doctor> doctors = new ArrayList<>();
}
