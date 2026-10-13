package com.maintenance.workorder.controller;

import com.maintenance.workorder.dto.MaintenanceTypeResponse;
import com.maintenance.workorder.service.MaintenanceTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MaintenanceTypeController — Exposes maintenance types for the frontend dropdown.
 *
 * GET /api/maintenance-types → Returns all available maintenance types
 */
@RestController
@RequestMapping("/api/maintenance-types")
public class MaintenanceTypeController {

    private final MaintenanceTypeService maintenanceTypeService;

    public MaintenanceTypeController(MaintenanceTypeService maintenanceTypeService) {
        this.maintenanceTypeService = maintenanceTypeService;
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceTypeResponse>> getAllMaintenanceTypes() {
        return ResponseEntity.ok(maintenanceTypeService.getAllMaintenanceTypes());
    }
}
