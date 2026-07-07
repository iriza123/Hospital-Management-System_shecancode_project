package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.HospitalRegistrationRequest;
import org.example.com.hospitalmanagementsystem.dto.response.AdminResponse;
import org.example.com.hospitalmanagementsystem.dto.response.HospitalResponse;
import org.example.com.hospitalmanagementsystem.entity.Admin;
import org.example.com.hospitalmanagementsystem.entity.Hospital;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.repository.AdminRepository;
import org.example.com.hospitalmanagementsystem.repository.HospitalRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public HospitalResponse registerHospital(HospitalRegistrationRequest request) {
        if (hospitalRepository.existsByHospitalNameIgnoreCase(request.getHospitalName())) {
            throw new BadRequestException("Hospital name already exists: " + request.getHospitalName());
        }
        if (adminRepository.existsByEmail(request.getAdmin().getEmail())) {
            throw new BadRequestException("Administrator email already exists: " + request.getAdmin().getEmail());
        }

        Hospital hospital = Hospital.builder()
                .hospitalName(request.getHospitalName())
                .telephone(request.getTelephone())
                .physicalAddress(request.getPhysicalAddress())
                .build();
        hospital = hospitalRepository.save(hospital);

        Admin admin = Admin.builder()
                .fullName(request.getAdmin().getFullName())
                .email(request.getAdmin().getEmail())
                .password(passwordEncoder.encode(request.getAdmin().getPassword()))
                .phoneNumber(request.getAdmin().getPhoneNumber())
                .hospital(hospital)
                .build();
        admin = adminRepository.save(admin);

        hospital.setAdmin(admin);

        return buildHospitalResponse(hospital, admin);
    }

    public HospitalResponse getHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
        return buildHospitalResponse(hospital, hospital.getAdmin());
    }

    public HospitalResponse updateHospital(Long id, HospitalRegistrationRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        if (!hospital.getHospitalName().equalsIgnoreCase(request.getHospitalName())
                && hospitalRepository.existsByHospitalNameIgnoreCase(request.getHospitalName())) {
            throw new BadRequestException("Hospital name already exists");
        }

        hospital.setHospitalName(request.getHospitalName());
        hospital.setTelephone(request.getTelephone());
        hospital.setPhysicalAddress(request.getPhysicalAddress());
        hospital = hospitalRepository.save(hospital);

        return buildHospitalResponse(hospital, hospital.getAdmin());
    }

    private HospitalResponse buildHospitalResponse(Hospital hospital, Admin admin) {
        AdminResponse adminResponse = null;
        if (admin != null) {
            adminResponse = AdminResponse.builder()
                    .id(admin.getId())
                    .fullName(admin.getFullName())
                    .email(admin.getEmail())
                    .phoneNumber(admin.getPhoneNumber())
                    .role(admin.getRole().name())
                    .build();
        }

        return HospitalResponse.builder()
                .id(hospital.getId())
                .hospitalName(hospital.getHospitalName())
                .telephone(hospital.getTelephone())
                .physicalAddress(hospital.getPhysicalAddress())
                .createdAt(hospital.getCreatedAt())
                .admin(adminResponse)
                .build();
    }
}
