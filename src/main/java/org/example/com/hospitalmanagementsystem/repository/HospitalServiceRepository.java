package org.example.com.hospitalmanagementsystem.repository;

import org.example.com.hospitalmanagementsystem.entity.HospitalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {
    List<HospitalService> findByHospitalId(Long hospitalId);
    boolean existsByServiceNameIgnoreCaseAndHospitalId(String serviceName, Long hospitalId);
    Optional<HospitalService> findByIdAndHospitalId(Long id, Long hospitalId);
}
