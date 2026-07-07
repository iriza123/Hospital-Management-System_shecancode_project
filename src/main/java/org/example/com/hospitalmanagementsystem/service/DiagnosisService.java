package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.DiagnosisRequest;
import org.example.com.hospitalmanagementsystem.dto.response.DiagnosisResponse;
import org.example.com.hospitalmanagementsystem.entity.Appointment;
import org.example.com.hospitalmanagementsystem.entity.Diagnosis;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.exception.UnauthorizedException;
import org.example.com.hospitalmanagementsystem.notification.EmailService;
import org.example.com.hospitalmanagementsystem.websocket.NotificationService;
import org.example.com.hospitalmanagementsystem.repository.AppointmentRepository;
import org.example.com.hospitalmanagementsystem.repository.DiagnosisRepository;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public DiagnosisResponse createOrUpdateDiagnosis(Long appointmentId, DiagnosisRequest request,
                                                      CustomUserDetails doctorDetails) {
        Appointment appointment = findAppointment(appointmentId);
        validateDoctorAccess(appointment, doctorDetails);
        validateNotCompleted(appointment);

        Diagnosis diagnosis;
        if (diagnosisRepository.existsByAppointmentId(appointmentId)) {
            diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));
        } else {
            diagnosis = new Diagnosis();
            diagnosis.setAppointment(appointment);
        }

        diagnosis.setSymptoms(request.getSymptoms());
        diagnosis.setDiagnosisNotes(request.getDiagnosisNotes());
        diagnosis.setRecommendedTreatment(request.getRecommendedTreatment());

        diagnosis = diagnosisRepository.save(diagnosis);
        return toResponse(diagnosis);
    }

    @Transactional
    public DiagnosisResponse uploadPrescription(Long appointmentId, MultipartFile file,
                                                 CustomUserDetails doctorDetails) throws IOException {
        Appointment appointment = findAppointment(appointmentId);
        validateDoctorAccess(appointment, doctorDetails);
        validateNotCompleted(appointment);

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BadRequestException("Only PDF files are allowed for prescriptions");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size must not exceed 5MB");
        }

        if (!diagnosisRepository.existsByAppointmentId(appointmentId)) {
            throw new BadRequestException("Please create a diagnosis before uploading a prescription");
        }

        Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        if (diagnosis.getPrescriptionFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(diagnosis.getPrescriptionFilePath()));
            } catch (IOException ignored) {}
        }

        diagnosis.setPrescriptionFilePath(filePath.toString());
        diagnosis.setPrescriptionFileName(file.getOriginalFilename());

        diagnosis = diagnosisRepository.save(diagnosis);
        
        emailService.sendDiagnosisUploadedEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getFullName()
        );

        notificationService.notifyDiagnosisUploaded(
                appointment.getPatient().getId(),
                appointmentId,
                appointment.getDoctor().getFullName()
        );

        return toResponse(diagnosis);
    }

    public DiagnosisResponse getDiagnosis(Long appointmentId, CustomUserDetails userDetails) {
        Appointment appointment = findAppointment(appointmentId);

        boolean isPatient = appointment.getPatient().getId().equals(userDetails.getUserId());
        boolean isDoctor = appointment.getDoctor().getId().equals(userDetails.getUserId());

        if (!isPatient && !isDoctor) {
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                throw new UnauthorizedException("You are not authorized to view this diagnosis");
            }
        }

        Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found for appointment: " + appointmentId));

        return toResponse(diagnosis);
    }

    private void validateDoctorAccess(Appointment appointment, CustomUserDetails doctorDetails) {
        if (!appointment.getDoctor().getId().equals(doctorDetails.getUserId())) {
            throw new UnauthorizedException("You can only diagnose your own patients");
        }
    }

    private void validateNotCompleted(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Completed appointments cannot be modified");
        }
    }

    private Appointment findAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    private DiagnosisResponse toResponse(Diagnosis diagnosis) {
        return DiagnosisResponse.builder()
                .id(diagnosis.getId())
                .symptoms(diagnosis.getSymptoms())
                .diagnosisNotes(diagnosis.getDiagnosisNotes())
                .recommendedTreatment(diagnosis.getRecommendedTreatment())
                .prescriptionFileName(diagnosis.getPrescriptionFileName())
                .appointmentId(diagnosis.getAppointment().getId())
                .createdAt(diagnosis.getCreatedAt())
                .build();
    }
}
