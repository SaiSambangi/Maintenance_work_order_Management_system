package com.maintenance.workorder.service;

import com.maintenance.workorder.dto.MaintenanceTypeResponse;
import com.maintenance.workorder.repository.MaintenanceTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MaintenanceTypeService — Provides read access to maintenance types.
 * Types are managed via the DataInitializer on startup.
 */
@Service
@Transactional(readOnly = true)
public class MaintenanceTypeService {

    private final MaintenanceTypeRepository maintenanceTypeRepository;

    public MaintenanceTypeService(MaintenanceTypeRepository maintenanceTypeRepository) {
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    public List<MaintenanceTypeResponse> getAllMaintenanceTypes() {
        return maintenanceTypeRepository.findAll()
                .stream()
                .map(MaintenanceTypeResponse::from)
                .collect(Collectors.toList());
    }
}
