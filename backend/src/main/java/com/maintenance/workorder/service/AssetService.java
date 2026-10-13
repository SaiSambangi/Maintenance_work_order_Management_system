package com.maintenance.workorder.service;

import com.maintenance.workorder.dto.AssetRequest;
import com.maintenance.workorder.dto.AssetResponse;
import com.maintenance.workorder.entity.Asset;
import com.maintenance.workorder.exception.DuplicateResourceException;
import com.maintenance.workorder.exception.ResourceNotFoundException;
import com.maintenance.workorder.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AssetService — Contains all business logic for asset management.
 *
 * The @Service annotation tells Spring this is a service component.
 * @Transactional ensures that database operations are wrapped in a
 * database transaction — if something goes wrong, all changes are rolled back.
 */
@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;

    /** Constructor injection — the recommended way to inject dependencies in Spring */
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Create a new asset.
     *
     * Steps:
     * 1. Check that assetId is not already taken (uniqueness)
     * 2. Check that serialNumber is not already taken (if provided)
     * 3. Map the request DTO to an Asset entity
     * 4. Save to database
     * 5. Return a response DTO
     */
    public AssetResponse createAsset(AssetRequest request) {
        // Check uniqueness of assetId
        if (assetRepository.existsByAssetId(request.getAssetId())) {
            throw new DuplicateResourceException(
                    "An asset with ID '" + request.getAssetId() + "' already exists"
            );
        }

        // Check uniqueness of serialNumber (only if provided)
        if (request.getSerialNumber() != null && !request.getSerialNumber().isBlank() &&
                assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException(
                    "An asset with serial number '" + request.getSerialNumber() + "' already exists"
            );
        }

        Asset asset = Asset.builder()
                .assetId(request.getAssetId())
                .assetType(request.getAssetType())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .serialNumber(request.getSerialNumber())
                .usageHours(request.getUsageHours())
                .usageCycles(request.getUsageCycles())
                .status(request.getStatus())
                .build();

        Asset saved = assetRepository.save(asset);
        return AssetResponse.from(saved);
    }

    /**
     * Get all assets.
     * @Transactional(readOnly=true) is a performance optimization — tells
     * Hibernate this is a read-only operation, so it can skip dirty checking.
     */
    @Transactional(readOnly = true)
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll()
                .stream()
                .map(AssetResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get one asset by its database ID.
     * Throws ResourceNotFoundException if not found → returns HTTP 404.
     */
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", id));
        return AssetResponse.from(asset);
    }

    /**
     * Update an existing asset.
     *
     * Steps:
     * 1. Find the asset (throw 404 if not found)
     * 2. Check uniqueness of the new assetId (make sure it's not taken by ANOTHER asset)
     * 3. Update the fields
     * 4. Save and return
     */
    public AssetResponse updateAsset(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", id));

        // Check uniqueness — but allow the same asset to keep its own assetId
        if (assetRepository.existsByAssetIdAndIdNot(request.getAssetId(), id)) {
            throw new DuplicateResourceException(
                    "Another asset already has ID '" + request.getAssetId() + "'"
            );
        }

        if (request.getSerialNumber() != null && !request.getSerialNumber().isBlank() &&
                assetRepository.existsBySerialNumberAndIdNot(request.getSerialNumber(), id)) {
            throw new DuplicateResourceException(
                    "Another asset already has serial number '" + request.getSerialNumber() + "'"
            );
        }

        // Update fields
        asset.setAssetId(request.getAssetId());
        asset.setAssetType(request.getAssetType());
        asset.setManufacturer(request.getManufacturer());
        asset.setModel(request.getModel());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setUsageHours(request.getUsageHours());
        asset.setUsageCycles(request.getUsageCycles());
        asset.setStatus(request.getStatus());

        // No need to call save() explicitly — @Transactional will detect the
        // changes and flush them to the database at the end of the method.
        return AssetResponse.from(asset);
    }
}
