package org.example.com.hospitalmanagementsystem.repository;

import org.example.com.hospitalmanagementsystem.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByAppointmentId(Long appointmentId);
    boolean existsByAppointmentId(Long appointmentId);
}
