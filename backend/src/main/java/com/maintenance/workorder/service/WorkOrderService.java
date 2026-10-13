package com.maintenance.workorder.service;

import com.maintenance.workorder.dto.*;
import com.maintenance.workorder.entity.*;
import com.maintenance.workorder.exception.*;
import com.maintenance.workorder.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * WorkOrderService — The most important service in this application.
 *
 * Enforces ALL business rules defined in the requirements:
 *
 * Rule 1: Work order number must be unique.
 * Rule 2: A CRITICAL work order must have an assigned technician.
 * Rule 3: A COMPLETED work order cannot return to OPEN.
 * Rule 4: A CANCELLED work order cannot move to IN_PROGRESS.
 * Rule 5: A new work order cannot have a scheduled date in the past.
 * Rule 6: An OUT_OF_SERVICE asset cannot receive a work order unless
 *         the maintenance type is marked as emergency.
 *
 * Also enforces valid status transitions:
 *   OPEN        → ASSIGNED, CANCELLED
 *   ASSIGNED    → IN_PROGRESS, CANCELLED
 *   IN_PROGRESS → COMPLETED
 */
@Service
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final AssetRepository assetRepository;
    private final TechnicianRepository technicianRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;
    private final MaintenanceHistoryRepository historyRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            AssetRepository assetRepository,
            TechnicianRepository technicianRepository,
            MaintenanceTypeRepository maintenanceTypeRepository,
            MaintenanceHistoryRepository historyRepository) {
        this.workOrderRepository = workOrderRepository;
        this.assetRepository = assetRepository;
        this.technicianRepository = technicianRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
        this.historyRepository = historyRepository;
    }

    // ============================================================
    //  CREATE
    // ============================================================

    /**
     * Create a new work order, enforcing all business rules.
     */
    public WorkOrderResponse createWorkOrder(WorkOrderRequest request) {

        // ---- Rule 1: Work order number must be unique ----
        if (workOrderRepository.existsByWorkOrderNumber(request.getWorkOrderNumber())) {
            throw new DuplicateResourceException(
                    "A work order with number '" + request.getWorkOrderNumber() + "' already exists"
            );
        }

        // ---- Load and validate the Asset ----
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asset not found with id: " + request.getAssetId()
                ));

        // ---- Load and validate the Maintenance Type ----
        MaintenanceType maintenanceType = maintenanceTypeRepository.findById(request.getMaintenanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance type not found with id: " + request.getMaintenanceTypeId()
                ));

        // ---- Rule 6: OUT_OF_SERVICE asset can only receive emergency work orders ----
        if (asset.getStatus() == AssetStatus.OUT_OF_SERVICE && !maintenanceType.isEmergency()) {
            throw new BusinessRuleException(
                    "Asset '" + asset.getAssetId() + "' is OUT_OF_SERVICE. " +
                    "Only emergency maintenance work orders can be created for it. " +
                    "Please select an emergency maintenance type."
            );
        }

        // ---- Load the Technician (optional) ----
        Technician technician = null;
        if (request.getAssignedTechnicianId() != null) {
            technician = technicianRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Technician not found with id: " + request.getAssignedTechnicianId()
                    ));
        }

        // ---- Rule 2: CRITICAL work orders must have an assigned technician ----
        if (request.getPriority() == Priority.CRITICAL && technician == null) {
            throw new BusinessRuleException(
                    "CRITICAL priority work orders must have an assigned technician. " +
                    "Please assign a technician before creating this work order."
            );
        }

        // ---- Rule 5: Scheduled date cannot be in the past ----
        if (request.getScheduledDate() != null &&
                request.getScheduledDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(
                    "Scheduled date cannot be in the past. " +
                    "Please select today or a future date."
            );
        }

        // ---- Build and save the work order ----
        WorkOrder workOrder = WorkOrder.builder()
                .workOrderNumber(request.getWorkOrderNumber())
                .asset(asset)
                .maintenanceType(maintenanceType)
                .description(request.getDescription())
                .priority(request.getPriority())
                .assignedTechnician(technician)
                .scheduledDate(request.getScheduledDate())
                .status(request.getStatus() != null ? request.getStatus() : WorkOrderStatus.OPEN)
                .build();

        WorkOrder saved = workOrderRepository.save(workOrder);

        // ---- Record creation in maintenance history ----
        recordHistory(saved, null, saved.getStatus(), "Work order created");

        return WorkOrderResponse.from(saved);
    }

    // ============================================================
    //  READ
    // ============================================================

    /**
     * Get all work orders, with optional filters.
     *
     * Uses JPA Specifications to build a dynamic WHERE clause.
     * For example: GET /api/work-orders?status=IN_PROGRESS&priority=HIGH
     *
     * @param status       Optional status filter
     * @param priority     Optional priority filter
     * @param assetId      Optional asset ID filter
     * @param technicianId Optional technician ID filter
     */
    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders(
            WorkOrderStatus status,
            Priority priority,
            Long assetId,
            Long technicianId) {

        // Build a dynamic Specification (SQL WHERE clause builder)
        Specification<WorkOrder> spec = Specification.where(null);

        if (status != null) {
            // Add: WHERE status = :status
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            // Add: AND priority = :priority
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priority"), priority));
        }
        if (assetId != null) {
            // Add: AND asset.id = :assetId
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("asset").get("id"), assetId));
        }
        if (technicianId != null) {
            // Add: AND assignedTechnician.id = :technicianId
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assignedTechnician").get("id"), technicianId));
        }

        return workOrderRepository.findAll(spec)
                .stream()
                .map(WorkOrderResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get a single work order by ID (with all related data loaded).
     */
    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order", id));
        return WorkOrderResponse.from(workOrder);
    }

    /**
     * Get the maintenance history for a work order.
     */
    @Transactional(readOnly = true)
    public List<MaintenanceHistoryResponse> getWorkOrderHistory(Long workOrderId) {
        // Make sure the work order exists
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException("Work order", workOrderId);
        }

        return historyRepository.findByWorkOrderIdOrderByChangedAtDesc(workOrderId)
                .stream()
                .map(MaintenanceHistoryResponse::from)
                .collect(Collectors.toList());
    }

    // ============================================================
    //  UPDATE
    // ============================================================

    /**
     * Update an existing work order, enforcing all business rules.
     */
    public WorkOrderResponse updateWorkOrder(Long id, WorkOrderRequest request) {
        WorkOrder workOrder = workOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order", id));

        // ---- Rule 1: Work order number uniqueness (allow same WO to keep its own number) ----
        if (workOrderRepository.existsByWorkOrderNumberAndIdNot(request.getWorkOrderNumber(), id)) {
            throw new DuplicateResourceException(
                    "Another work order already has number '" + request.getWorkOrderNumber() + "'"
            );
        }

        // ---- Validate status transition ----
        WorkOrderStatus currentStatus = workOrder.getStatus();
        WorkOrderStatus newStatus = request.getStatus();
        validateStatusTransition(currentStatus, newStatus);

        // ---- Load and validate Asset ----
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asset not found with id: " + request.getAssetId()
                ));

        // ---- Load and validate Maintenance Type ----
        MaintenanceType maintenanceType = maintenanceTypeRepository.findById(request.getMaintenanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance type not found with id: " + request.getMaintenanceTypeId()
                ));

        // ---- Load Technician (optional) ----
        Technician technician = null;
        if (request.getAssignedTechnicianId() != null) {
            technician = technicianRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Technician not found with id: " + request.getAssignedTechnicianId()
                    ));
        }

        // ---- Rule 2: CRITICAL must have technician ----
        if (request.getPriority() == Priority.CRITICAL && technician == null) {
            throw new BusinessRuleException(
                    "CRITICAL priority work orders must have an assigned technician."
            );
        }

        // ---- Rule 5: Scheduled date cannot be in the past (only enforce on OPEN/ASSIGNED) ----
        if (request.getScheduledDate() != null &&
                request.getScheduledDate().isBefore(LocalDate.now()) &&
                (newStatus == WorkOrderStatus.OPEN || newStatus == WorkOrderStatus.ASSIGNED)) {
            throw new BusinessRuleException(
                    "Scheduled date cannot be in the past."
            );
        }

        // ---- Record status change in history (if status changed) ----
        if (!currentStatus.equals(newStatus)) {
            // Set completedDate when transitioning to COMPLETED
            if (newStatus == WorkOrderStatus.COMPLETED) {
                workOrder.setCompletedDate(LocalDateTime.now());
            }
            recordHistory(workOrder, currentStatus, newStatus, null);
        }

        // ---- Update work order fields ----
        workOrder.setWorkOrderNumber(request.getWorkOrderNumber());
        workOrder.setAsset(asset);
        workOrder.setMaintenanceType(maintenanceType);
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(request.getPriority());
        workOrder.setAssignedTechnician(technician);
        workOrder.setScheduledDate(request.getScheduledDate());
        workOrder.setStatus(newStatus);

        return WorkOrderResponse.from(workOrder);
    }

    // ============================================================
    //  PRIVATE HELPERS
    // ============================================================

    /**
     * Validates that a status transition is allowed.
     *
     * Valid transitions defined in requirements:
     *   OPEN        → ASSIGNED, CANCELLED
     *   ASSIGNED    → IN_PROGRESS, CANCELLED
     *   IN_PROGRESS → COMPLETED
     *   COMPLETED   → (none — terminal state)
     *   CANCELLED   → (none — terminal state)
     *
     * Business Rules enforced here:
     *   Rule 3: COMPLETED cannot return to OPEN
     *   Rule 4: CANCELLED cannot move to IN_PROGRESS
     */
    private void validateStatusTransition(WorkOrderStatus from, WorkOrderStatus to) {
        // If status hasn't changed, no validation needed
        if (from.equals(to)) {
            return;
        }

        // Define allowed transitions as a map
        boolean isValid = switch (from) {
            case OPEN        -> to == WorkOrderStatus.ASSIGNED   || to == WorkOrderStatus.CANCELLED;
            case ASSIGNED    -> to == WorkOrderStatus.IN_PROGRESS || to == WorkOrderStatus.CANCELLED;
            case IN_PROGRESS -> to == WorkOrderStatus.COMPLETED;
            case COMPLETED   -> false;  // Rule 3: COMPLETED is terminal
            case CANCELLED   -> false;  // Rule 4: CANCELLED is terminal
        };

        if (!isValid) {
            // Give a very clear error message explaining what went wrong
            String reason = "";
            if (from == WorkOrderStatus.COMPLETED) {
                reason = " A COMPLETED work order cannot be changed.";
            } else if (from == WorkOrderStatus.CANCELLED) {
                reason = " A CANCELLED work order cannot be changed.";
            }

            throw new BusinessRuleException(
                    "Invalid status transition from " + from + " to " + to + "." + reason +
                    " Allowed transitions from " + from + ": " + getAllowedTransitions(from)
            );
        }
    }

    /** Returns a human-readable list of allowed next statuses */
    private String getAllowedTransitions(WorkOrderStatus status) {
        return switch (status) {
            case OPEN        -> "ASSIGNED, CANCELLED";
            case ASSIGNED    -> "IN_PROGRESS, CANCELLED";
            case IN_PROGRESS -> "COMPLETED";
            case COMPLETED   -> "none (terminal state)";
            case CANCELLED   -> "none (terminal state)";
        };
    }

    /**
     * Records a status change in the maintenance_history table.
     * Called whenever a work order is created or its status changes.
     */
    private void recordHistory(WorkOrder workOrder, WorkOrderStatus previousStatus,
                               WorkOrderStatus newStatus, String notes) {
        MaintenanceHistory history = MaintenanceHistory.builder()
                .workOrder(workOrder)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .notes(notes)
                .build();
        historyRepository.save(history);
    }
}
