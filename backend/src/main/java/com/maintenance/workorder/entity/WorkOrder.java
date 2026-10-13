package com.maintenance.workorder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a maintenance Work Order — the core entity of this system.
 *
 * Maps to the 'work_orders' table in MySQL.
 *
 * A work order links an Asset to a MaintenanceType and optionally
 * assigns a Technician to perform the work.
 *
 * Relationships:
 *   - ManyToOne with Asset (many work orders can be for one asset)
 *   - ManyToOne with MaintenanceType
 *   - ManyToOne with Technician (optional — assigned later)
 */
@Entity
@Table(name = "work_orders", indexes = {
    @Index(name = "idx_wo_number", columnList = "work_order_number"),
    @Index(name = "idx_wo_status", columnList = "status"),
    @Index(name = "idx_wo_priority", columnList = "priority"),
    @Index(name = "idx_wo_asset", columnList = "asset_id"),
    @Index(name = "idx_wo_technician", columnList = "technician_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for this work order, e.g. "WO-2024-001".
     * Must be unique (enforced by DB unique constraint AND business rule).
     */
    @Column(name = "work_order_number", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Work order number is required")
    private String workOrderNumber;

    /**
     * The asset that needs maintenance.
     * Uses a foreign key (asset_id) pointing to the assets table.
     * FetchType.LAZY = don't load asset data until it's actually needed (performance).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @NotNull(message = "Asset is required")
    private Asset asset;

    /**
     * The type of maintenance to be performed.
     * Uses a foreign key (maintenance_type_id) pointing to maintenance_types table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_type_id", nullable = false)
    @NotNull(message = "Maintenance type is required")
    private MaintenanceType maintenanceType;

    /**
     * Detailed description of what needs to be done.
     */
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Priority level: LOW, MEDIUM, HIGH, CRITICAL.
     * CRITICAL work orders require an assigned technician (Business Rule #2).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @NotNull(message = "Priority is required")
    private Priority priority;

    /**
     * The technician assigned to this work order.
     * This is optional — a work order starts as OPEN with no technician.
     * When a technician is assigned, status moves to ASSIGNED.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician assignedTechnician;

    /**
     * The planned date for this maintenance work to be done.
     * Cannot be in the past (Business Rule #5).
     */
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    /**
     * Current lifecycle status: OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Status is required")
    private WorkOrderStatus status;

    /**
     * Timestamp when this work order was first created in the database.
     * Set automatically by Hibernate — we never set this manually.
     */
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp when the work order was marked as COMPLETED.
     * Set by the service layer when status transitions to COMPLETED.
     */
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
}
