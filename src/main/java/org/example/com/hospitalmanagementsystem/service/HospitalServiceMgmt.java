package org.example.com.hospitalmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.HospitalServiceRequest;
import org.example.com.hospitalmanagementsystem.dto.response.HospitalServiceResponse;
import org.example.com.hospitalmanagementsystem.entity.Hospital;
import org.example.com.hospitalmanagementsystem.entity.HospitalService;
import org.example.com.hospitalmanagementsystem.exception.BadRequestException;
import org.example.com.hospitalmanagementsystem.exception.ResourceNotFoundException;
import org.example.com.hospitalmanagementsystem.repository.HospitalRepository;
import org.example.com.hospitalmanagementsystem.repository.HospitalServiceRepository;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalServiceMgmt {

    private final HospitalServiceRepository serviceRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public HospitalServiceResponse createService(HospitalServiceRequest request, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);

        if (serviceRepository.existsByServiceNameIgnoreCaseAndHospitalId(request.getServiceName(), hospital.getId())) {
            throw new BadRequestException("Service name already exists in this hospital: " + request.getServiceName());
        }

        HospitalService service = HospitalService.builder()
                .serviceName(request.getServiceName())
                .description(request.getDescription())
                .hospital(hospital)
                .build();

        service = serviceRepository.save(service);
        return toResponse(service);
    }

    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public HospitalServiceResponse updateService(Long serviceId, HospitalServiceRequest request, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        HospitalService service = serviceRepository.findByIdAndHospitalId(serviceId, hospital.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        if (!service.getServiceName().equalsIgnoreCase(request.getServiceName())
                && serviceRepository.existsByServiceNameIgnoreCaseAndHospitalId(request.getServiceName(), hospital.getId())) {
            throw new BadRequestException("Service name already exists: " + request.getServiceName());
        }

        service.setServiceName(request.getServiceName());
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        service = serviceRepository.save(service);
        return toResponse(service);
    }

    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public void deleteService(Long serviceId, CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        HospitalService service = serviceRepository.findByIdAndHospitalId(serviceId, hospital.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        service.getDoctors().forEach(doctor -> doctor.getServices().remove(service));
        serviceRepository.delete(service);
    }

    @Cacheable(value = "services", key = "'hospital_' + #adminDetails.userId")
    public List<HospitalServiceResponse> getServices(CustomUserDetails adminDetails) {
        Hospital hospital = getAdminHospital(adminDetails);
        return serviceRepository.findByHospitalId(hospital.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "services", key = "'all'")
    public List<HospitalServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HospitalServiceResponse getServiceById(Long id) {
        HospitalService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return toResponse(service);
    }

    private Hospital getAdminHospital(CustomUserDetails adminDetails) {
        return hospitalRepository.findAll().stream()
                .filter(h -> h.getAdmin() != null && h.getAdmin().getId().equals(adminDetails.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found for this administrator"));
    }

    public HospitalServiceResponse toResponse(HospitalService service) {
        return HospitalServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .description(service.getDescription())
                .hospitalId(service.getHospital().getId())
                .hospitalName(service.getHospital().getHospitalName())
                .createdAt(service.getCreatedAt())
                .build();
    }
}
