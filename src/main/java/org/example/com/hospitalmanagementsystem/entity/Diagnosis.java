package org.example.com.hospitalmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symptoms", nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "diagnosis_notes", columnDefinition = "TEXT")
    private String diagnosisNotes;

    @Column(name = "recommended_treatment")
    private String recommendedTreatment;

    @Column(name = "prescription_file_path")
    private String prescriptionFilePath;

    @Column(name = "prescription_file_name")
    private String prescriptionFileName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;
}
