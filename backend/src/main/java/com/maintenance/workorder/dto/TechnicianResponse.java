package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.Technician;
import com.maintenance.workorder.entity.TechnicianStatus;
import lombok.*;

/**
 * DTO for sending Technician data back to the client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianResponse {

    private Long id;
    private String name;
    private String skill;
    private TechnicianStatus status;

    /** Factory method to convert entity to response DTO */
    public static TechnicianResponse from(Technician technician) {
        return TechnicianResponse.builder()
                .id(technician.getId())
                .name(technician.getName())
                .skill(technician.getSkill())
                .status(technician.getStatus())
                .build();
    }
}
