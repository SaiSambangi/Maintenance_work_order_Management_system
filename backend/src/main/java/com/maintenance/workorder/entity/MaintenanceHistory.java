package com.maintenance.workorder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Records every status change that happens to a work order.
 *
 * This gives us a full audit trail / maintenance history.
 * For example: "WO-001 went from OPEN to ASSIGNED at 2024-03-15 09:30"
 *
 * Maps to the 'maintenance_history' table in MySQL.
 */
@Entity
@Table(name = "maintenance_history", indexes = {
    @Index(name = "idx_history_work_order", columnList = "work_order_id"),
    @Index(name = "idx_history_changed_at", columnList = "changed_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceHistory {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The work order this history record belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    /**
     * What status the work order was BEFORE this change.
     * Null if this is the first record (creation).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private WorkOrderStatus previousStatus;

    /**
     * What status the work order became AFTER this change.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private WorkOrderStatus newStatus;

    /**
     * Optional note about why the change was made.
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * When this status change happened.
     * Set automatically by Hibernate.
     */
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}
