package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.AssetStatus;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data Transfer Object for creating or updating an Asset.
 *
 * Why DTOs? We don't expose entities directly to the API because:
 * 1. Entities might have fields we don't want clients to set (like auto-generated IDs)
 * 2. We can add different validation rules for create vs. update
 * 3. It decouples the API contract from the database schema
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetRequest {

    @NotBlank(message = "Asset ID is required")
    @Size(max = 100, message = "Asset ID cannot exceed 100 characters")
    private String assetId;

    @NotBlank(message = "Asset type is required")
    private String assetType;

    private String manufacturer;
    private String model;
    private String serialNumber;

    @Min(value = 0, message = "Usage hours cannot be negative")
    private Integer usageHours;

    @Min(value = 0, message = "Usage cycles cannot be negative")
    private Integer usageCycles;

    @NotNull(message = "Status is required")
    private AssetStatus status;
}
