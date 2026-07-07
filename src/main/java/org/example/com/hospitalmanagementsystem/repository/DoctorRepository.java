package org.example.com.hospitalmanagementsystem.repository;

import org.example.com.hospitalmanagementsystem.entity.Appointment;
import org.example.com.hospitalmanagementsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);
    List<Doctor> findByHospitalId(Long hospitalId);
    List<Doctor> findByHospitalIdAndActiveTrue(Long hospitalId);

    @Query("SELECT d FROM Doctor d JOIN d.services s WHERE s.id = :serviceId AND d.active = true")
    List<Doctor> findActiveByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
           "AND a.status NOT IN ('REJECTED', 'CANCELLED')")
    long countAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :date " +
           "AND a.status NOT IN ('REJECTED','CANCELLED','COMPLETED')")
    List<org.example.com.hospitalmanagementsystem.entity.Appointment> findFutureAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId, @Param("date") LocalDate date);
}
