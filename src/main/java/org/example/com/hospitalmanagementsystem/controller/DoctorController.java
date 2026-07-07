package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.DoctorCreateRequest;
import org.example.com.hospitalmanagementsystem.dto.request.DoctorUpdateRequest;
import org.example.com.hospitalmanagementsystem.dto.response.DoctorResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // Admin creates a doctor
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createDoctor(request, userDetails));
    }

    // Admin updates doctor
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request, userDetails));
    }

    // Admin toggles doctor active status
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> toggleStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(doctorService.toggleDoctorStatus(id, userDetails));
    }

    // Admin deletes doctor (only if no future appointments)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        doctorService.deleteDoctor(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    // Admin views all doctors in their hospital
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorResponse>> getDoctors(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(doctorService.getDoctors(userDetails));
    }

    // Admin views doctor by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // Doctor views own profile
    @GetMapping("/my/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(doctorService.getDoctorProfile(userDetails));
    }
}
