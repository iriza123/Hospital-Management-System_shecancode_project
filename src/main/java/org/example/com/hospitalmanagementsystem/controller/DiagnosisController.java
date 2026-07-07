package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.DiagnosisRequest;
import org.example.com.hospitalmanagementsystem.dto.response.DiagnosisResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.DiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/appointments/{appointmentId}/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> createDiagnosis(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DiagnosisRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diagnosisService.createOrUpdateDiagnosis(appointmentId, request, userDetails));
    }

    @PutMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> updateDiagnosis(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DiagnosisRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(diagnosisService.createOrUpdateDiagnosis(appointmentId, request, userDetails));
    }

    @PostMapping(value = "/prescription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> uploadPrescription(
            @PathVariable Long appointmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        return ResponseEntity.ok(diagnosisService.uploadPrescription(appointmentId, file, userDetails));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiagnosisResponse> getDiagnosis(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(diagnosisService.getDiagnosis(appointmentId, userDetails));
    }
}
