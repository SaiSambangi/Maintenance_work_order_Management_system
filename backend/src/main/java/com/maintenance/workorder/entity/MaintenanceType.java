package com.maintenance.workorder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a type of maintenance that can be performed on an asset.
 * Examples: "Preventive Maintenance", "Corrective Repair", "Emergency Inspection"
 *
 * Maps to the 'maintenance_types' table in MySQL.
 *
 * The 'isEmergency' flag is critical: when true, work orders of this type
 * can be created for OUT_OF_SERVICE assets (Business Rule #6).
 */
@Entity
@Table(name = "maintenance_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceType {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short name for this maintenance type, e.g. "Preventive", "Emergency Repair"
     * Must be unique.
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Maintenance type name is required")
    private String name;

    /**
     * Longer explanation of what this type involves.
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * If true, work orders of this type can be assigned to OUT_OF_SERVICE assets.
     * This flag enables Business Rule #6: emergency maintenance on broken assets.
     */
    @Column(name = "is_emergency", nullable = false)
    private boolean isEmergency;
}
