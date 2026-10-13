package com.maintenance.workorder.dto;

import com.maintenance.workorder.entity.Asset;
import com.maintenance.workorder.entity.AssetStatus;
import lombok.*;

/**
 * Data Transfer Object for sending Asset data back to the client.
 *
 * This is what the JSON response looks like when you GET /api/assets.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {

    private Long id;
    private String assetId;
    private String assetType;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private Integer usageHours;
    private Integer usageCycles;
    private AssetStatus status;

    /**
     * Factory method: converts an Asset entity to an AssetResponse DTO.
     * This keeps the mapping logic in one place.
     */
    public static AssetResponse from(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetId(asset.getAssetId())
                .assetType(asset.getAssetType())
                .manufacturer(asset.getManufacturer())
                .model(asset.getModel())
                .serialNumber(asset.getSerialNumber())
                .usageHours(asset.getUsageHours())
                .usageCycles(asset.getUsageCycles())
                .status(asset.getStatus())
                .build();
    }
}
