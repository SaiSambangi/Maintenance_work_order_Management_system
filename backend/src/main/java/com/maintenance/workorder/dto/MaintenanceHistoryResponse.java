package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.MaintenanceHistory;
import com.maintenance.workorder.entity.WorkOrderStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for sending maintenance history records back to the client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceHistoryResponse {

    private Long id;
    private WorkOrderStatus previousStatus;
    private WorkOrderStatus newStatus;
    private String notes;
    private LocalDateTime changedAt;

    public static MaintenanceHistoryResponse from(MaintenanceHistory history) {
        return MaintenanceHistoryResponse.builder()
                .id(history.getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .notes(history.getNotes())
                .changedAt(history.getChangedAt())
                .build();
    }
}
