package com.maintenance.workorder.repository;

import com.maintenance.workorder.entity.Technician;
import com.maintenance.workorder.entity.TechnicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Technician database operations.
 * Spring Data JPA auto-generates standard CRUD operations.
 */
@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    /** Find all technicians with a specific status (e.g., all AVAILABLE technicians) */
    List<Technician> findByStatus(TechnicianStatus status);
}
