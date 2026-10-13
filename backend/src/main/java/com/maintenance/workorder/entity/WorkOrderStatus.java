package com.maintenance.workorder.entity;

/**
 * Represents the lifecycle status of a Work Order.
 *
 * Valid transitions:
 *   OPEN        → ASSIGNED, CANCELLED
 *   ASSIGNED    → IN_PROGRESS, CANCELLED
 *   IN_PROGRESS → COMPLETED
 *   COMPLETED   → (terminal — no further transitions allowed)
 *   CANCELLED   → (terminal — no further transitions allowed)
 */
public enum WorkOrderStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
