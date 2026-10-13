package com.maintenance.workorder.entity;

/**
 * Priority level for work orders.
 *
 * CRITICAL - Must be handled immediately; requires an assigned technician
 * HIGH     - Urgent, should be handled soon
 * MEDIUM   - Normal priority
 * LOW      - Can be done when resources are available
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
