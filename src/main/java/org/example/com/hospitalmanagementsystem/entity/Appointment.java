package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "symptoms", nullable = false)
    private String symptoms;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private HospitalService service;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Diagnosis diagnosis;
}
