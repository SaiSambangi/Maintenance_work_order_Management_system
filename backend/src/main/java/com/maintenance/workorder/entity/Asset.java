package com.maintenance.workorder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a physical asset (equipment/machine) in the company.
 *
 * Maps to the 'assets' table in MySQL.
 * Each asset has a unique assetId (like a barcode or tag number).
 */
@Entity
@Table(name = "assets", indexes = {
    @Index(name = "idx_asset_id", columnList = "asset_id"),
    @Index(name = "idx_asset_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    /** Auto-generated primary key (1, 2, 3, ...) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The human-readable asset identifier, e.g. "ASSET-001" or "EQ-2024-A5".
     * Must be unique across all assets.
     */
    @Column(name = "asset_id", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Asset ID is required")
    @Size(max = 100, message = "Asset ID cannot exceed 100 characters")
    private String assetId;

    /**
     * Category of the asset, e.g. "HVAC", "Generator", "Pump"
     */
    @Column(name = "asset_type", nullable = false, length = 100)
    @NotBlank(message = "Asset type is required")
    private String assetType;

    /**
     * The company that made this asset, e.g. "Siemens", "Caterpillar"
     */
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    /**
     * The specific model name/number, e.g. "XR-5000"
     */
    @Column(name = "model", length = 100)
    private String model;

    /**
     * Unique serial number printed on the physical asset.
     * Must be unique across all assets.
     */
    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    /**
     * How many hours the asset has been in operation.
     * Used to determine when scheduled maintenance is due.
     */
    @Column(name = "usage_hours")
    @Min(value = 0, message = "Usage hours cannot be negative")
    private Integer usageHours;

    /**
     * How many operational cycles the asset has completed (e.g., for engines).
     */
    @Column(name = "usage_cycles")
    @Min(value = 0, message = "Usage cycles cannot be negative")
    private Integer usageCycles;

    /**
     * Current status of the asset.
     * Stored as a string in the database (e.g., "ACTIVE") for readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Asset status is required")
    private AssetStatus status;
}
