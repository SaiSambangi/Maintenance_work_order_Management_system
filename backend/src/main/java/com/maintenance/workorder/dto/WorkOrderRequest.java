package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.Priority;
import com.maintenance.workorder.entity.WorkOrderStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating a new Work Order.
 *
 * The client sends JSON like:
 * {
 *   "workOrderNumber": "WO-2024-001",
 *   "assetId": 1,
 *   "maintenanceTypeId": 2,
 *   "description": "Replace filters",
 *   "priority": "HIGH",
 *   "assignedTechnicianId": 3,
 *   "scheduledDate": "2024-12-01",
 *   "status": "OPEN"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderRequest {

    @NotBlank(message = "Work order number is required")
    @Size(max = 100, message = "Work order number cannot exceed 100 characters")
    private String workOrderNumber;

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Maintenance type ID is required")
    private Long maintenanceTypeId;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    /** Optional: ID of the technician to assign. Can be null for OPEN work orders. */
    private Long assignedTechnicianId;

    private LocalDate scheduledDate;

    @NotNull(message = "Status is required")
    private WorkOrderStatus status;
}
