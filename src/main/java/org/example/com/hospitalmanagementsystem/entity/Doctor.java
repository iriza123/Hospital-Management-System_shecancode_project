package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.com.hospitalmanagementsystem.enums.Gender;
import org.example.com.hospitalmanagementsystem.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_license_number", nullable = false, unique = true)
    private String medicalLicenseNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "specialisation", nullable = false)
    private String specialisation;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.DOCTOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_services",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<HospitalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
