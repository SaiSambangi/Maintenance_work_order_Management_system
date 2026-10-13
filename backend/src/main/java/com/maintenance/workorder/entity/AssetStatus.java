package com.maintenance.workorder.entity;

/**
 * Represents the operational status of a physical asset.
 *
 * ACTIVE       - Asset is working and available for use
 * INACTIVE     - Asset is not in use but still functional
 * UNDER_MAINTENANCE - Asset is currently being repaired
 * OUT_OF_SERVICE    - Asset is broken/unusable (special rules apply)
 * DECOMMISSIONED    - Asset has been retired permanently
 */
public enum AssetStatus {
    ACTIVE,
    INACTIVE,
    UNDER_MAINTENANCE,
    OUT_OF_SERVICE,
    DECOMMISSIONED
}
