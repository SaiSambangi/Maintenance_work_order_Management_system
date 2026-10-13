package com.maintenance.workorder.service;

import com.maintenance.workorder.dto.TechnicianRequest;
import com.maintenance.workorder.dto.TechnicianResponse;
import com.maintenance.workorder.entity.Technician;
import com.maintenance.workorder.exception.ResourceNotFoundException;
import com.maintenance.workorder.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TechnicianService — Contains business logic for technician management.
 */
@Service
@Transactional
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    /**
     * Register a new technician.
     */
    public TechnicianResponse createTechnician(TechnicianRequest request) {
        Technician technician = Technician.builder()
                .name(request.getName())
                .skill(request.getSkill())
                .status(request.getStatus())
                .build();

        Technician saved = technicianRepository.save(technician);
        return TechnicianResponse.from(saved);
    }

    /**
     * Get all technicians.
     */
    @Transactional(readOnly = true)
    public List<TechnicianResponse> getAllTechnicians() {
        return technicianRepository.findAll()
                .stream()
                .map(TechnicianResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get one technician by ID.
     */
    @Transactional(readOnly = true)
    public TechnicianResponse getTechnicianById(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", id));
        return TechnicianResponse.from(technician);
    }
}
