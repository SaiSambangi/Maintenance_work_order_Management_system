package com.maintenance.workorder.config;

import com.maintenance.workorder.entity.*;
import com.maintenance.workorder.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer — Seeds the database with initial sample data on startup.
 *
 * CommandLineRunner: Spring calls the run() method automatically after the
 * application starts. We use it to populate the database with test data
 * so you can immediately test the APIs without adding data manually.
 *
 * IMPORTANT: This only inserts data if the tables are empty (to avoid
 * duplicate data on restarts).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AssetRepository assetRepository;
    private final TechnicianRepository technicianRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;

    public DataInitializer(
            AssetRepository assetRepository,
            TechnicianRepository technicianRepository,
            MaintenanceTypeRepository maintenanceTypeRepository) {
        this.assetRepository = assetRepository;
        this.technicianRepository = technicianRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    @Override
    public void run(String... args) {
        seedMaintenanceTypes();
        seedAssets();
        seedTechnicians();
    }

    /** Insert sample maintenance types if none exist */
    private void seedMaintenanceTypes() {
        if (maintenanceTypeRepository.count() > 0) {
            log.info("Maintenance types already exist, skipping seed.");
            return;
        }

        maintenanceTypeRepository.save(MaintenanceType.builder()
                .name("Preventive Maintenance")
                .description("Scheduled routine maintenance to prevent failures")
                .isEmergency(false)
                .build());

        maintenanceTypeRepository.save(MaintenanceType.builder()
                .name("Corrective Repair")
                .description("Fixing a known defect or malfunction")
                .isEmergency(false)
                .build());

        maintenanceTypeRepository.save(MaintenanceType.builder()
                .name("Emergency Repair")
                .description("Urgent repair for critical failures — can be applied to OUT_OF_SERVICE assets")
                .isEmergency(true)
                .build());

        maintenanceTypeRepository.save(MaintenanceType.builder()
                .name("Inspection")
                .description("Routine inspection and assessment of asset condition")
                .isEmergency(false)
                .build());

        maintenanceTypeRepository.save(MaintenanceType.builder()
                .name("Predictive Maintenance")
                .description("Maintenance triggered by sensor data or condition monitoring")
                .isEmergency(false)
                .build());

        log.info("Seeded 5 maintenance types.");
    }

    /** Insert sample assets if none exist */
    private void seedAssets() {
        if (assetRepository.count() > 0) {
            log.info("Assets already exist, skipping seed.");
            return;
        }

        assetRepository.save(Asset.builder()
                .assetId("ASSET-001")
                .assetType("HVAC System")
                .manufacturer("Carrier")
                .model("50XC-060")
                .serialNumber("SN-HVAC-001")
                .usageHours(4500)
                .usageCycles(900)
                .status(AssetStatus.ACTIVE)
                .build());

        assetRepository.save(Asset.builder()
                .assetId("ASSET-002")
                .assetType("Generator")
                .manufacturer("Caterpillar")
                .model("C15")
                .serialNumber("SN-GEN-002")
                .usageHours(12000)
                .usageCycles(2400)
                .status(AssetStatus.ACTIVE)
                .build());

        assetRepository.save(Asset.builder()
                .assetId("ASSET-003")
                .assetType("Water Pump")
                .manufacturer("Grundfos")
                .model("CM5-5")
                .serialNumber("SN-PUMP-003")
                .usageHours(8200)
                .usageCycles(1640)
                .status(AssetStatus.UNDER_MAINTENANCE)
                .build());

        assetRepository.save(Asset.builder()
                .assetId("ASSET-004")
                .assetType("Elevator")
                .manufacturer("Otis")
                .model("Gen2-MR")
                .serialNumber("SN-ELEV-004")
                .usageHours(22000)
                .usageCycles(110000)
                .status(AssetStatus.ACTIVE)
                .build());

        assetRepository.save(Asset.builder()
                .assetId("ASSET-005")
                .assetType("Compressor")
                .manufacturer("Atlas Copco")
                .model("GA30")
                .serialNumber("SN-COMP-005")
                .usageHours(5500)
                .usageCycles(1100)
                .status(AssetStatus.OUT_OF_SERVICE)
                .build());

        log.info("Seeded 5 assets.");
    }

    /** Insert sample technicians if none exist */
    private void seedTechnicians() {
        if (technicianRepository.count() > 0) {
            log.info("Technicians already exist, skipping seed.");
            return;
        }

        technicianRepository.save(Technician.builder()
                .name("Alice Johnson")
                .skill("Electrical")
                .status(TechnicianStatus.AVAILABLE)
                .build());

        technicianRepository.save(Technician.builder()
                .name("Bob Martinez")
                .skill("Mechanical")
                .status(TechnicianStatus.AVAILABLE)
                .build());

        technicianRepository.save(Technician.builder()
                .name("Carol Chen")
                .skill("HVAC")
                .status(TechnicianStatus.BUSY)
                .build());

        technicianRepository.save(Technician.builder()
                .name("David Kim")
                .skill("Plumbing")
                .status(TechnicianStatus.AVAILABLE)
                .build());

        technicianRepository.save(Technician.builder()
                .name("Eva Patel")
                .skill("Electronics")
                .status(TechnicianStatus.ON_LEAVE)
                .build());

        log.info("Seeded 5 technicians.");
    }
}
