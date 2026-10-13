package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.MaintenanceType;
import lombok.*;

/**
 * DTO for sending MaintenanceType data back to the client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTypeResponse {

    private Long id;
    private String name;
    private String description;
    private boolean isEmergency;

    public static MaintenanceTypeResponse from(MaintenanceType mt) {
        return MaintenanceTypeResponse.builder()
                .id(mt.getId())
                .name(mt.getName())
                .description(mt.getDescription())
                .isEmergency(mt.isEmergency())
                .build();
    }
}
