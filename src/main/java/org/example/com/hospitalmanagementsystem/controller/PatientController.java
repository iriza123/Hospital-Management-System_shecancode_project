package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.PatientRegistrationRequest;
import org.example.com.hospitalmanagementsystem.dto.request.PatientUpdateRequest;
import org.example.com.hospitalmanagementsystem.dto.response.PatientResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // Public registration
    @PostMapping("/register")
    public ResponseEntity<PatientResponse> register(@Valid @RequestBody PatientRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.register(request));
    }

    // Patient: view own profile
    @GetMapping("/my/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getProfile(userDetails));
    }

    // Patient: full update profile
    @PutMapping("/my/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(patientService.updateProfile(userDetails.getUserId(), request, userDetails));
    }

    // Patient: partial update profile (PATCH)
    @PatchMapping("/my/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponse> patchMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(patientService.updateProfile(userDetails.getUserId(), request, userDetails));
    }

    // Patient: delete own account
    @DeleteMapping("/my/account")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.deleteAccount(userDetails.getUserId(), userDetails);
        return ResponseEntity.noContent().build();
    }

    // Admin: get all patients
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // Admin: get patient by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientByIdForAdmin(id));
    }
}
