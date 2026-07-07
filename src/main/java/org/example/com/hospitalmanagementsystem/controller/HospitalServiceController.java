package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.HospitalServiceRequest;
import org.example.com.hospitalmanagementsystem.dto.response.HospitalServiceResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.HospitalServiceMgmt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HospitalServiceController {

    private final HospitalServiceMgmt hospitalServiceMgmt;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalServiceResponse> createService(
            @Valid @RequestBody HospitalServiceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hospitalServiceMgmt.createService(request, userDetails));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody HospitalServiceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(hospitalServiceMgmt.updateService(id, request, userDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        hospitalServiceMgmt.deleteService(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<HospitalServiceResponse>> getServices(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(hospitalServiceMgmt.getServices(userDetails));
        }
        return ResponseEntity.ok(hospitalServiceMgmt.getAllServices());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HospitalServiceResponse> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalServiceMgmt.getServiceById(id));
    }
}
