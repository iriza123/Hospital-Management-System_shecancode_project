package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.com.hospitalmanagementsystem.enums.Role;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ADMIN;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;
}
