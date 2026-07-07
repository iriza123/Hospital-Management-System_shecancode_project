package org.example.com.hospitalmanagementsystem.repository;

import org.example.com.hospitalmanagementsystem.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    boolean existsByHospitalNameIgnoreCase(String hospitalName);
    Optional<Hospital> findByHospitalNameIgnoreCase(String hospitalName);
}
