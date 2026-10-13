package com.maintenance.workorder.entity;

/**
 * Represents the employment/availability status of a technician.
 *
 * AVAILABLE    - Technician is free and can be assigned work orders
 * BUSY         - Technician is currently working on something
 * ON_LEAVE     - Technician is on leave/vacation
 * INACTIVE     - Technician is no longer active in the system
 */
public enum TechnicianStatus {
    AVAILABLE,
    BUSY,
    ON_LEAVE,
    INACTIVE
}
