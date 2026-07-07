package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.com.hospitalmanagementsystem.enums.Gender;
import org.example.com.hospitalmanagementsystem.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_id", unique = true)
    private String nationalId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.PATIENT;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
}
