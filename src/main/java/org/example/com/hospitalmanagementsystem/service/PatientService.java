package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.PatientRegistrationRequest;
import org.example.com.hospitalmanagementsystem.dto.request.PatientUpdateRequest;
import org.example.com.hospitalmanagementsystem.dto.response.PatientResponse;
import org.example.com.hospitalmanagementsystem.entity.Patient;
import org.example.com.hospitalmanagementsystem.enums.AppointmentStatus;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.exception.UnauthorizedException;
import org.example.com.hospitalmanagementsystem.notification.EmailService;
import org.example.com.hospitalmanagementsystem.repository.AppointmentRepository;
import org.example.com.hospitalmanagementsystem.repository.PatientRepository;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public PatientResponse register(PatientRegistrationRequest request) {
        validateRegistration(request);

        int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 0) {
            throw new BadRequestException("Patient age cannot be negative");
        }

        Patient patient = Patient.builder()
                .nationalId(request.getNationalId())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        patient = patientRepository.save(patient);
        
        emailService.sendWelcomeEmail(patient.getEmail(), patient.getFullName());
        
        return toResponse(patient);
    }

    public PatientResponse getProfile(CustomUserDetails userDetails) {
        Patient patient = getPatientById(userDetails.getUserId());
        return toResponse(patient);
    }

    @Cacheable(value = "patients", key = "#id")
    public PatientResponse getPatientByIdForAdmin(Long id) {
        return toResponse(getPatientById(id));
    }

    @Cacheable(value = "patients", key = "'all'")
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#patientId"),
            @CacheEvict(value = "patients", key = "'all'")
    })
    public PatientResponse updateProfile(Long patientId, PatientUpdateRequest request, CustomUserDetails userDetails) {
        validateOwnership(patientId, userDetails);
        Patient patient = getPatientById(patientId);

        if (request.getEmail() != null && !request.getEmail().equals(patient.getEmail())
                && patientRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (request.getNationalId() != null && !request.getNationalId().equals(patient.getNationalId())
                && patientRepository.existsByNationalId(request.getNationalId())) {
            throw new BadRequestException("National ID already in use");
        }

        if (request.getNationalId() != null) patient.setNationalId(request.getNationalId());
        if (request.getFullName() != null) patient.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getEmail() != null) patient.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) patient.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getEmergencyContactName() != null) patient.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) patient.setEmergencyContactPhone(request.getEmergencyContactPhone());

        patient = patientRepository.save(patient);
        return toResponse(patient);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#patientId"),
            @CacheEvict(value = "patients", key = "'all'")
    })
    public void deleteAccount(Long patientId, CustomUserDetails userDetails) {
        validateOwnership(patientId, userDetails);
        Patient patient = getPatientById(patientId);

        List<?> activeAppointments = appointmentRepository.findActiveByPatientId(patientId);
        if (!activeAppointments.isEmpty()) {
            throw new BadRequestException("Cannot delete account with pending or approved appointments. Please cancel them first.");
        }

        patientRepository.delete(patient);
    }

    private void validateRegistration(PatientRegistrationRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        if (request.getNationalId() != null && !request.getNationalId().isBlank()
                && patientRepository.existsByNationalId(request.getNationalId())) {
            throw new BadRequestException("National ID already registered: " + request.getNationalId());
        }
    }

    private void validateOwnership(Long patientId, CustomUserDetails userDetails) {
        if (!userDetails.getUserId().equals(patientId)) {
            throw new UnauthorizedException("You are not authorized to modify this patient's data");
        }
    }

    private Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    public PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .nationalId(patient.getNationalId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .role(patient.getRole().name())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
