package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.TechnicianStatus;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for creating or updating a Technician.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianRequest {

    @NotBlank(message = "Technician name is required")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String name;

    private String skill;

    @NotNull(message = "Status is required")
    private TechnicianStatus status;
}
