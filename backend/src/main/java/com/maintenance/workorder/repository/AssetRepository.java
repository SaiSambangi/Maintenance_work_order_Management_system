package com.maintenance.workorder.repository;

import com.maintenance.workorder.entity.Asset;
import com.maintenance.workorder.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Asset database operations.
 *
 * By extending JpaRepository, Spring Data JPA automatically generates:
 *   - save(), findById(), findAll(), delete(), count(), etc.
 *
 * We only need to add custom query methods here.
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /** Check if an asset with this assetId already exists (for uniqueness validation) */
    boolean existsByAssetId(String assetId);

    /** Check if another asset already has this assetId (used during updates) */
    boolean existsByAssetIdAndIdNot(String assetId, Long id);

    /** Check if an asset with this serial number already exists */
    boolean existsBySerialNumber(String serialNumber);

    /** Check if another asset has this serial number (used during updates) */
    boolean existsBySerialNumberAndIdNot(String serialNumber, Long id);

    /** Find all assets with a specific status */
    List<Asset> findByStatus(AssetStatus status);
}
