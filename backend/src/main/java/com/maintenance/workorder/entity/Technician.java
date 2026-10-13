package com.maintenance.workorder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a maintenance technician in the system.
 *
 * Maps to the 'technicians' table in MySQL.
 */
@Entity
@Table(name = "technicians", indexes = {
    @Index(name = "idx_technician_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    /** Auto-generated primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the technician, e.g. "John Smith"
     */
    @Column(name = "name", nullable = false, length = 150)
    @NotBlank(message = "Technician name is required")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String name;

    /**
     * Technical specialty, e.g. "Electrical", "Mechanical", "HVAC", "Plumbing"
     */
    @Column(name = "skill", length = 100)
    private String skill;

    /**
     * Current status of the technician (AVAILABLE, BUSY, ON_LEAVE, INACTIVE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Technician status is required")
    private TechnicianStatus status;
}
