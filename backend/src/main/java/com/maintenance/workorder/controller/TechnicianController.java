package com.maintenance.workorder.controller;

import com.maintenance.workorder.dto.TechnicianRequest;
import com.maintenance.workorder.dto.TechnicianResponse;
import com.maintenance.workorder.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TechnicianController — Handles HTTP requests for Technician management.
 *
 * Endpoints:
 *   POST   /api/technicians        → Register a new technician
 *   GET    /api/technicians        → Get all technicians
 *   GET    /api/technicians/{id}   → Get one technician by ID
 */
@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    /** POST /api/technicians — Register a new technician */
    @PostMapping
    public ResponseEntity<TechnicianResponse> createTechnician(
            @Valid @RequestBody TechnicianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(technicianService.createTechnician(request));
    }

    /** GET /api/technicians — Get all technicians */
    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    /** GET /api/technicians/{id} — Get one technician by ID */
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> getTechnicianById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.getTechnicianById(id));
    }
}
