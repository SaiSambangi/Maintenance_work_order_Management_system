package com.maintenance.workorder.repository;

import com.maintenance.workorder.entity.MaintenanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MaintenanceHistory database operations.
 */
@Repository
public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {

    /**
     * Get all history records for a work order, ordered by most recent first.
     * This gives us the full audit trail for a work order.
     */
    List<MaintenanceHistory> findByWorkOrderIdOrderByChangedAtDesc(Long workOrderId);
}
