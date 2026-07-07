package org.example.com.hospitalmanagementsystem.repository;

import org.example.com.hospitalmanagementsystem.entity.Appointment;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date AND a.status NOT IN ('REJECTED', 'CANCELLED')")
    long countByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
            Long doctorId, LocalDate date, LocalTime time, List<AppointmentStatus> statuses);

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
            Long patientId, LocalDate date, LocalTime time, List<AppointmentStatus> statuses);

    List<Appointment> findByAppointmentDate(LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status IN ('PENDING','APPROVED')")
    List<Appointment> findActiveByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :date " +
           "AND a.status NOT IN ('REJECTED','CANCELLED','COMPLETED')")
    List<Appointment> findFutureByDoctorId(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT a.service.id, a.service.serviceName, COUNT(a) FROM Appointment a " +
           "GROUP BY a.service.id, a.service.serviceName ORDER BY COUNT(a) DESC")
    List<Object[]> countAppointmentsPerService();

    @Query("SELECT a.doctor.id, a.doctor.fullName, COUNT(a) FROM Appointment a " +
           "WHERE a.status NOT IN ('REJECTED','CANCELLED') " +
           "GROUP BY a.doctor.id, a.doctor.fullName")
    List<Object[]> doctorWorkload();

    long countByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date")
    long countByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN :start AND :end")
    long countByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
