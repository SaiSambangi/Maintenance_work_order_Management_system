package com.maintenance.workorder.repository;

import com.maintenance.workorder.entity.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MaintenanceType database operations.
 */
@Repository
public interface MaintenanceTypeRepository extends JpaRepository<MaintenanceType, Long> {

    /** Find all maintenance types that are (or are not) emergency types */
    List<MaintenanceType> findByIsEmergency(boolean isEmergency);
}
