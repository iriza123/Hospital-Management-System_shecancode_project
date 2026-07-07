package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.response.ReportResponse;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;
import org.example.com.hospitalmanagementsystem.repository.AppointmentRepository;
import org.example.com.hospitalmanagementsystem.repository.DoctorRepository;
import org.example.com.hospitalmanagementsystem.repository.PatientRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Cacheable(value = "reports", key = "'full_report'")
    public ReportResponse generateReport() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        long dailyAppointments = appointmentRepository.countByDate(today);
        long weeklyAppointments = appointmentRepository.countByDateBetween(weekStart, weekEnd);
        long monthlyAppointments = appointmentRepository.countByDateBetween(monthStart, monthEnd);

        List<Object[]> serviceData = appointmentRepository.countAppointmentsPerService();
        List<Map<String, Object>> appointmentsPerService = new ArrayList<>();
        String mostRequestedService = null;
        long maxCount = 0;

        for (Object[] row : serviceData) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("serviceId", row[0]);
            entry.put("serviceName", row[1]);
            entry.put("appointmentCount", row[2]);
            appointmentsPerService.add(entry);

            long count = ((Number) row[2]).longValue();
            if (count > maxCount) {
                maxCount = count;
                mostRequestedService = (String) row[1];
            }
        }

        List<Object[]> workloadData = appointmentRepository.doctorWorkload();
        List<Map<String, Object>> doctorWorkload = new ArrayList<>();
        for (Object[] row : workloadData) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("doctorId", row[0]);
            entry.put("doctorName", row[1]);
            entry.put("appointmentCount", row[2]);
            doctorWorkload.add(entry);
        }

        return ReportResponse.builder()
                .totalPatients(patientRepository.count())
                .totalDoctors(doctorRepository.count())
                .totalAppointments(appointmentRepository.count())
                .pendingAppointments(appointmentRepository.countByStatus(AppointmentStatus.PENDING))
                .approvedAppointments(appointmentRepository.countByStatus(AppointmentStatus.APPROVED))
                .completedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED))
                .cancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED))
                .rejectedAppointments(appointmentRepository.countByStatus(AppointmentStatus.REJECTED))
                .dailyAppointments(dailyAppointments)
                .weeklyAppointments(weeklyAppointments)
                .monthlyAppointments(monthlyAppointments)
                .appointmentsPerService(appointmentsPerService)
                .doctorWorkload(doctorWorkload)
                .mostRequestedService(mostRequestedService)
                .build();
    }
}
