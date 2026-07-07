package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.AppointmentReassignRequest;
import org.example.com.hospitalmanagementsystem.dto.request.AppointmentRequest;
import org.example.com.hospitalmanagementsystem.dto.response.AppointmentResponse;
import org.example.com.hospitalmanagementsystem.dto.response.DiagnosisResponse;
import org.example.com.hospitalmanagementsystem.entity.Appointment;
import org.example.com.hospitalmanagementsystem.entity.Diagnosis;
import org.example.com.hospitalmanagementsystem.entity.Doctor;
import org.example.com.hospitalmanagementsystem.entity.HospitalService;
import org.example.com.hospitalmanagementsystem.entity.Patient;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.exception.UnauthorizedException;
import org.example.com.hospitalmanagementsystem.notification.EmailService;
import org.example.com.hospitalmanagementsystem.websocket.NotificationService;
import org.example.com.hospitalmanagementsystem.repository.AppointmentRepository;
import org.example.com.hospitalmanagementsystem.repository.DoctorRepository;
import org.example.com.hospitalmanagementsystem.repository.HospitalServiceRepository;
import org.example.com.hospitalmanagementsystem.repository.PatientRepository;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalServiceRepository serviceRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    private static final int MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY = 4;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request, CustomUserDetails patientDetails) {
        Patient patient = patientRepository.findById(patientDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        if (!doctor.isActive()) {
            throw new BadRequestException("Doctor is not available");
        }

        HospitalService service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceId()));

        boolean doctorBelongsToService = doctor.getServices().stream()
                .anyMatch(s -> s.getId().equals(service.getId()));
        if (!doctorBelongsToService) {
            throw new BadRequestException("Doctor is not assigned to the selected service");
        }

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Appointment date cannot be in the past");
        }

        long doctorDayCount = appointmentRepository.countByDoctorAndDate(doctor.getId(), request.getAppointmentDate());
        if (doctorDayCount >= MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY) {
            throw new BadRequestException("Doctor has reached the maximum number of appointments (4) for this date. Please choose another date or doctor.");
        }

        List<AppointmentStatus> excludedStatuses = List.of(AppointmentStatus.REJECTED, AppointmentStatus.CANCELLED);
        boolean doctorTimeConflict = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime(), excludedStatuses);
        if (doctorTimeConflict) {
            throw new BadRequestException("Doctor already has an appointment at this time");
        }

        boolean patientTimeConflict = appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        patient.getId(), request.getAppointmentDate(), request.getAppointmentTime(), excludedStatuses);
        if (patientTimeConflict) {
            throw new BadRequestException("You already have an appointment at this date and time");
        }

        Appointment appointment = Appointment.builder()
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .symptoms(request.getSymptoms())
                .status(AppointmentStatus.PENDING)
                .patient(patient)
                .doctor(doctor)
                .service(service)
                .build();

        appointment = appointmentRepository.save(appointment);
        
        emailService.sendAppointmentBookedEmail(
                patient.getEmail(),
                patient.getFullName(),
                request.getAppointmentDate().toString(),
                request.getAppointmentTime().toString()
        );

        notificationService.notifyAppointmentBooked(
                patient.getId(),
                appointment.getId(),
                doctor.getFullName(),
                request.getAppointmentDate().toString()
        );

        return toResponse(appointment);
    }

    public List<AppointmentResponse> getMyAppointments(CustomUserDetails patientDetails) {
        return appointmentRepository.findByPatientId(patientDetails.getUserId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(CustomUserDetails doctorDetails) {
        return appointmentRepository.findByDoctorId(doctorDetails.getUserId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public AppointmentResponse approveAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Only PENDING appointments can be approved");
        }
        appointment.setStatus(AppointmentStatus.APPROVED);
        AppointmentResponse response = toResponse(appointmentRepository.save(appointment));
        
        emailService.sendAppointmentApprovedEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getFullName(),
                appointment.getAppointmentDate().toString(),
                appointment.getAppointmentTime().toString()
        );

        notificationService.notifyAppointmentApproved(
                appointment.getPatient().getId(),
                appointment.getId(),
                appointment.getDoctor().getFullName(),
                appointment.getAppointmentDate().toString()
        );

        return response;
    }

    @Transactional
    public AppointmentResponse rejectAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Only PENDING appointments can be rejected");
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        AppointmentResponse response = toResponse(appointmentRepository.save(appointment));
        
        emailService.sendAppointmentRejectedEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFullName(),
                appointment.getAppointmentDate().toString()
        );

        notificationService.notifyAppointmentRejected(
                appointment.getPatient().getId(),
                appointment.getId(),
                appointment.getAppointmentDate().toString()
        );

        return response;
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getStatus() != AppointmentStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED appointments can be completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, CustomUserDetails patientDetails) {
        Appointment appointment = findById(appointmentId);

        if (!appointment.getPatient().getId().equals(patientDetails.getUserId())) {
            throw new UnauthorizedException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Completed appointments cannot be cancelled");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot cancel an appointment after the appointment date");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        AppointmentResponse response = toResponse(appointmentRepository.save(appointment));

        notificationService.notifyAppointmentCancelled(
                appointment.getPatient().getId(),
                appointment.getId(),
                appointment.getAppointmentDate().toString()
        );

        return response;
    }

    @Transactional
    public AppointmentResponse reassignAppointment(Long appointmentId, AppointmentReassignRequest request) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Completed appointments cannot be reassigned");
        }

        Doctor newDoctor = doctorRepository.findById(request.getNewDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getNewDoctorId()));

        if (!newDoctor.isActive()) {
            throw new BadRequestException("New doctor is not active");
        }

        boolean belongsToService = newDoctor.getServices().stream()
                .anyMatch(s -> s.getId().equals(appointment.getService().getId()));
        if (!belongsToService) {
            throw new BadRequestException("New doctor is not assigned to this appointment's service");
        }

        long count = appointmentRepository.countByDoctorAndDate(newDoctor.getId(), appointment.getAppointmentDate());
        if (count >= MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY) {
            throw new BadRequestException("New doctor has reached the maximum appointments for this date");
        }

        appointment.setDoctor(newDoctor);
        return toResponse(appointmentRepository.save(appointment));
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        DiagnosisResponse diagnosisResponse = null;
        if (appointment.getDiagnosis() != null) {
            Diagnosis d = appointment.getDiagnosis();
            diagnosisResponse = DiagnosisResponse.builder()
                    .id(d.getId())
                    .symptoms(d.getSymptoms())
                    .diagnosisNotes(d.getDiagnosisNotes())
                    .recommendedTreatment(d.getRecommendedTreatment())
                    .prescriptionFileName(d.getPrescriptionFileName())
                    .appointmentId(appointment.getId())
                    .createdAt(d.getCreatedAt())
                    .build();
        }

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .symptoms(appointment.getSymptoms())
                .status(appointment.getStatus())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFullName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getFullName())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getServiceName())
                .diagnosis(diagnosisResponse)
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
