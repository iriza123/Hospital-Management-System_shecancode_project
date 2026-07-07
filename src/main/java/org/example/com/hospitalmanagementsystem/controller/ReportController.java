package org.example.com.hospitalmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.com.hospitalmanagementsystem.dto.response.ReportResponse;
import org.example.com.hospitalmanagementsystem.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> getReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}
