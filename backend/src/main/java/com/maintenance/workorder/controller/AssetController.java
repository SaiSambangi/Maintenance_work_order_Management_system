package com.maintenance.workorder.controller;

import com.maintenance.workorder.dto.AssetRequest;
import com.maintenance.workorder.dto.AssetResponse;
import com.maintenance.workorder.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AssetController — Handles HTTP requests for Asset management.
 *
 * @RestController = @Controller + @ResponseBody
 *   It means: "this class handles HTTP requests and returns JSON"
 *
 * @RequestMapping("/api/assets") = all methods in this class
 *   handle URLs starting with /api/assets
 *
 * The controller layer is kept thin — it just:
 * 1. Receives the HTTP request
 * 2. Calls the service layer (where the real logic lives)
 * 3. Returns the HTTP response
 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * POST /api/assets
     * Creates a new asset.
     *
     * @Valid triggers Bean Validation — checks the @NotBlank, @NotNull, etc.
     * annotations on AssetRequest before passing it to the service.
     *
     * Returns HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest request) {
        AssetResponse response = assetService.createAsset(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/assets
     * Returns a list of all assets.
     */
    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    /**
     * GET /api/assets/{id}
     * Returns one asset by its database ID.
     * Returns 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    /**
     * PUT /api/assets/{id}
     * Updates an existing asset.
     * Returns 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }
}
