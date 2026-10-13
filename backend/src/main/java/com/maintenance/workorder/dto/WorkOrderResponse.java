package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for sending full Work Order details back to the client.
 *
 * Instead of returning raw IDs, we include the nested asset and technician
 * details so the frontend doesn't need to make extra API calls.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderResponse {

    private Long id;
    private String workOrderNumber;

    /** Full asset details (not just the ID) */
    private AssetResponse asset;

    /** Full maintenance type details */
    private MaintenanceTypeResponse maintenanceType;

    private String description;
    private Priority priority;

    /** Full technician details (or null if no technician assigned yet) */
    private TechnicianResponse assignedTechnician;

    private LocalDate scheduledDate;
    private WorkOrderStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;

    /**
     * Factory method: converts a WorkOrder entity to a WorkOrderResponse DTO.
     * Calls the nested entity-to-DTO conversions for asset, maintenanceType, etc.
     */
    public static WorkOrderResponse from(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId());
        response.setWorkOrderNumber(workOrder.getWorkOrderNumber());
        response.setDescription(workOrder.getDescription());
        response.setPriority(workOrder.getPriority());
        response.setScheduledDate(workOrder.getScheduledDate());
        response.setStatus(workOrder.getStatus());
        response.setCreatedDate(workOrder.getCreatedDate());
        response.setCompletedDate(workOrder.getCompletedDate());

        if (workOrder.getAsset() != null) {
            response.setAsset(AssetResponse.from(workOrder.getAsset()));
        }
        if (workOrder.getMaintenanceType() != null) {
            response.setMaintenanceType(MaintenanceTypeResponse.from(workOrder.getMaintenanceType()));
        }
        if (workOrder.getAssignedTechnician() != null) {
            response.setAssignedTechnician(TechnicianResponse.from(workOrder.getAssignedTechnician()));
        }

        return response;
    }
}
