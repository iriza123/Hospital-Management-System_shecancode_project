package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.DoctorCreateRequest;
import org.example.com.hospitalmanagementsystem.dto.request.DoctorUpdateRequest;
import org.example.com.hospitalmanagementsystem.dto.response.DoctorResponse;
import org.example.com.hospitalmanagementsystem.dto.response.HospitalServiceResponse;
import org.example.com.hospitalmanagementsystem.entity.Doctor;
import org.example.com.hospitalmanagementsystem.entity.Hospital;
import org.example.com.hospitalmanagementsystem.entity.HospitalService;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.notification.EmailService;
import org.example.com.hospitalmanagementsystem.repository.DoctorRepository;
import org.example.com.hospitalmanagementsystem.repository.HospitalRepository;
import org.example.com.hospitalmanagementsystem.repository.HospitalServiceRepository;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "doctors", allEntries = true)
    })
    public DoctorResponse createDoctor(DoctorCreateRequest request, CustomUserDetails adminDetails) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Doctor email already exists: " + request.getEmail());
        }
        if (doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
            throw new BadRequestException("Medical license number already exists: " + request.getMedicalLicenseNumber());
        }

        Hospital hospital = getAdminHospital(adminDetails);

        List<Long> serviceIds = request.getServiceIds();
        long distinctCount = serviceIds.stream().distinct().count();
        if (distinctCount != serviceIds.size()) {
            throw new BadRequestException("Duplicate service IDs provided");
        }

        List<HospitalService> services = serviceIds.stream()
                .map(id -> serviceRepository.findByIdAndHospitalId(id, hospital.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id)))
                .collect(Collectors.toList());

        Doctor doctor = Doctor.builder()
                .medicalLicenseNumber(request.getMedicalLicenseNumber())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .specialisation(request.getSpecialisation())
                .active(true)
                .hospital(hospital)
                .services(services)
                .build();

        doctor = doctorRepository.save(doctor);
        
        emailService.sendDoctorCreatedEmail(doctor.getEmail(), doctor.getFullName(), request.getEmail(), request.getPassword());
        
        return toResponse(doctor);
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "doctors", key = "#doctorId") },
            evict = { @CacheEvict(value = "doctors", allEntries = true) }
    )
    public DoctorResponse updateDoctor(Long doctorId, DoctorUpdateRequest request, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        Doctor doctor = getDoctorInHospital(doctorId, hospital.getId());

        if (request.getEmail() != null && !request.getEmail().equals(doctor.getEmail())
                && doctorRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        if (request.getFullName() != null) doctor.setFullName(request.getFullName());
        if (request.getEmail() != null) doctor.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) doctor.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) doctor.setGender(request.getGender());
        if (request.getSpecialisation() != null) doctor.setSpecialisation(request.getSpecialisation());

        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            long distinctCount = request.getServiceIds().stream().distinct().count();
            if (distinctCount != request.getServiceIds().size()) {
                throw new BadRequestException("Duplicate service IDs provided");
            }
            List<HospitalService> services = request.getServiceIds().stream()
                    .map(id -> serviceRepository.findByIdAndHospitalId(id, hospital.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id)))
                    .collect(Collectors.toList());
            doctor.setServices(services);
        }

        doctor = doctorRepository.save(doctor);
        return toResponse(doctor);
    }

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public DoctorResponse toggleDoctorStatus(Long doctorId, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        Doctor doctor = getDoctorInHospital(doctorId, hospital.getId());
        doctor.setActive(!doctor.isActive());
        doctor = doctorRepository.save(doctor);
        return toResponse(doctor);
    }

    @Transactional
    @CacheEvict(value = "doctors", allEntries = true)
    public void deleteDoctor(Long doctorId, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        Doctor doctor = getDoctorInHospital(doctorId, hospital.getId());

        List<?> futureAppointments = doctorRepository.findFutureAppointmentsByDoctorId(doctorId, LocalDate.now());
        if (!futureAppointments.isEmpty()) {
            throw new BadRequestException("Doctor cannot be deleted because they have future appointments. Please deactivate instead.");
        }

        doctorRepository.delete(doctor);
    }

    @Cacheable(value = "doctors", key = "'hospital_' + #adminDetails.userId")
    public List<DoctorResponse> getDoctors(CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        return doctorRepository.findByHospitalId(hospital.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse getDoctorProfile(CustomUserDetails userDetails) {
        Doctor doctor = doctorRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return toResponse(doctor);
    }

    @Cacheable(value = "doctors", key = "#id")
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return toResponse(doctor);
    }

    private Doctor getDoctorInHospital(Long doctorId, Long hospitalId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        if (!doctor.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Doctor does not belong to your hospital");
        }
        return doctor;
    }

    private Hospital getAdminHospital(CustomUserDetails adminDetails) {
        return hospitalRepository.findAll().stream()
                .filter(h -> h.getAdmin() != null && h.getAdmin().getId().equals(adminDetails.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found for this administrator"));
    }

    public DoctorResponse toResponse(Doctor doctor) {
        List<HospitalServiceResponse> serviceResponses = doctor.getServices().stream()
                .map(s -> HospitalServiceResponse.builder()
                        .id(s.getId())
                        .serviceName(s.getServiceName())
                        .description(s.getDescription())
                        .hospitalId(s.getHospital().getId())
                        .hospitalName(s.getHospital().getHospitalName())
                        .build())
                .collect(Collectors.toList());

        return DoctorResponse.builder()
                .id(doctor.getId())
                .medicalLicenseNumber(doctor.getMedicalLicenseNumber())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .phoneNumber(doctor.getPhoneNumber())
                .gender(doctor.getGender())
                .specialisation(doctor.getSpecialisation())
                .active(doctor.isActive())
                .role(doctor.getRole().name())
                .hospitalId(doctor.getHospital().getId())
                .hospitalName(doctor.getHospital().getHospitalName())
                .services(serviceResponses)
                .createdAt(doctor.getCreatedAt())
                .build();
    }
}
