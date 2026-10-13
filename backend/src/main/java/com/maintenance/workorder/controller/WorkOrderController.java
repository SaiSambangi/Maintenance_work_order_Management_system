package com.maintenance.workorder.controller;

import com.maintenance.workorder.dto.*;
import com.maintenance.workorder.entity.Priority;
import com.maintenance.workorder.entity.WorkOrderStatus;
import com.maintenance.workorder.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WorkOrderController — Handles HTTP requests for Work Order management.
 *
 * Endpoints:
 *   POST   /api/work-orders                    → Create a work order
 *   GET    /api/work-orders                    → Get all (with optional filters)
 *   GET    /api/work-orders/{id}               → Get one by ID
 *   PUT    /api/work-orders/{id}               → Update a work order
 *   GET    /api/work-orders/{id}/history       → Get maintenance history
 *
 * Filter query parameters (can be combined):
 *   ?status=IN_PROGRESS
 *   ?priority=HIGH
 *   ?asset_id=123
 *   ?technician_id=456
 *
 * Example: GET /api/work-orders?status=OPEN&priority=CRITICAL
 */
@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * POST /api/work-orders
     * Creates a new work order. All business rules are enforced in the service.
     * Returns HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody WorkOrderRequest request) {
        WorkOrderResponse response = workOrderService.createWorkOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/work-orders
     * Returns all work orders, filtered by optional query parameters.
     *
     * @param status       Optional: filter by status (e.g., "OPEN", "IN_PROGRESS")
     * @param priority     Optional: filter by priority (e.g., "HIGH", "CRITICAL")
     * @param assetId      Optional: filter by asset ID
     * @param technicianId Optional: filter by technician ID
     *
     * @RequestParam(required = false) means these parameters are optional.
     * Spring will automatically convert "IN_PROGRESS" to WorkOrderStatus.IN_PROGRESS.
     */
    @GetMapping
    public ResponseEntity<List<WorkOrderResponse>> getAllWorkOrders(
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(name = "asset_id", required = false) Long assetId,
            @RequestParam(name = "technician_id", required = false) Long technicianId) {

        List<WorkOrderResponse> workOrders =
                workOrderService.getAllWorkOrders(status, priority, assetId, technicianId);
        return ResponseEntity.ok(workOrders);
    }

    /**
     * GET /api/work-orders/{id}
     * Returns full details of one work order.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }

    /**
     * PUT /api/work-orders/{id}
     * Updates a work order (including status transitions).
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderRequest request) {
        return ResponseEntity.ok(workOrderService.updateWorkOrder(id, request));
    }

    /**
     * GET /api/work-orders/{id}/history
     * Returns the full audit trail of status changes for this work order.
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<MaintenanceHistoryResponse>> getWorkOrderHistory(
            @PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderHistory(id));
    }
}
