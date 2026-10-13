package com.maintenance.workorder.repository;

import com.maintenance.workorder.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for WorkOrder database operations.
 *
 * We also extend JpaSpecificationExecutor so we can build dynamic
 * filter queries (for search/filter functionality).
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>,
        JpaSpecificationExecutor<WorkOrder> {

    /** Check if a work order with this number already exists (Business Rule #1) */
    boolean existsByWorkOrderNumber(String workOrderNumber);

    /** Check if ANOTHER work order already has this number (for updates) */
    boolean existsByWorkOrderNumberAndIdNot(String workOrderNumber, Long id);

    /** Find all work orders for a specific asset */
    List<WorkOrder> findByAssetId(Long assetId);

    /** Find all work orders assigned to a specific technician */
    List<WorkOrder> findByAssignedTechnicianId(Long technicianId);

    /** Find all work orders with a specific status */
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    /** Find all work orders with a specific priority */
    List<WorkOrder> findByPriority(Priority priority);

    /**
     * Eagerly load all related data in one query to avoid N+1 problem.
     * N+1 problem: if you load 10 work orders and each has an asset,
     * Hibernate would make 11 queries (1 + 10). This JOIN FETCH avoids that.
     */
    @Query("SELECT w FROM WorkOrder w " +
           "LEFT JOIN FETCH w.asset " +
           "LEFT JOIN FETCH w.maintenanceType " +
           "LEFT JOIN FETCH w.assignedTechnician " +
           "WHERE w.id = :id")
    Optional<WorkOrder> findByIdWithDetails(@Param("id") Long id);
}
