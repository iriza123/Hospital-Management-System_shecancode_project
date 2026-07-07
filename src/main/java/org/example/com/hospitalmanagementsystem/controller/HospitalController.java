package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.HospitalRegistrationRequest;
import org.example.com.hospitalmanagementsystem.dto.response.HospitalResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping("/register")
    public ResponseEntity<HospitalResponse> register(@Valid @RequestBody HospitalRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalService.registerHospital(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalResponse> getHospital(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospital(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalResponse> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(hospitalService.updateHospital(id, request));
    }
}
