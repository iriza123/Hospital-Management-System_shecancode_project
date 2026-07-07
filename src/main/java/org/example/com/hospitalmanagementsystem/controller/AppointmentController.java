package org.example.com.hospitalmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.request.AppointmentReassignRequest;
import org.example.com.hospitalmanagementsystem.dto.request.AppointmentRequest;
import org.example.com.hospitalmanagementsystem.dto.response.AppointmentResponse;
import org.example.com.hospitalmanagementsystem.authentication.CustomUserDetails;
import org.example.com.hospitalmanagementsystem.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Patient books an appointment
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(request, userDetails));
    }

    // Patient views own appointments
    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(userDetails));
    }

    // Patient cancels appointment
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, userDetails));
    }

    // Doctor views their appointments
    @GetMapping("/doctor/my")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(userDetails));
    }

    // Doctor completes appointment
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    // Admin views all appointments
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // Admin views appointment by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // Admin approves appointment
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> approveAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.approveAppointment(id));
    }

    // Admin rejects appointment
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> rejectAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id));
    }

    // Admin reassigns appointment
    @PatchMapping("/{id}/reassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> reassignAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentReassignRequest request) {
        return ResponseEntity.ok(appointmentService.reassignAppointment(id, request));
    }
}
