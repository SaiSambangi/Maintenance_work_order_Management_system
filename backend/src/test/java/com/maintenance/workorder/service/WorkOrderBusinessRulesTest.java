package com.maintenance.workorder.service;

import com.maintenance.workorder.dto.WorkOrderRequest;
import com.maintenance.workorder.dto.WorkOrderResponse;
import com.maintenance.workorder.entity.*;
import com.maintenance.workorder.exception.BusinessRuleException;
import com.maintenance.workorder.exception.DuplicateResourceException;
import com.maintenance.workorder.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests specifically verifying the 6 mandatory business rules.
 */
@ExtendWith(MockitoExtension.class)
class WorkOrderBusinessRulesTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private MaintenanceTypeRepository maintenanceTypeRepository;

    @Mock
    private MaintenanceHistoryRepository historyRepository;

    @InjectMocks
    private WorkOrderService workOrderService;

    private Asset sampleAsset;
    private MaintenanceType regularType;
    private MaintenanceType emergencyType;
    private Technician sampleTechnician;

    @BeforeEach
    void setUp() {
        sampleAsset = Asset.builder()
                .id(1L)
                .assetId("ASSET-100")
                .assetType("Compressor")
                .status(AssetStatus.ACTIVE)
                .build();

        regularType = MaintenanceType.builder()
                .id(1L)
                .name("Preventive")
                .isEmergency(false)
                .build();

        emergencyType = MaintenanceType.builder()
                .id(2L)
                .name("Emergency Repair")
                .isEmergency(true)
                .build();

        sampleTechnician = Technician.builder()
                .id(1L)
                .name("Alice")
                .status(TechnicianStatus.AVAILABLE)
                .build();
    }

    @Test
    @DisplayName("Rule 1: Work Order Number must be unique")
    void testRule1_DuplicateWorkOrderNumber() {
        when(workOrderRepository.existsByWorkOrderNumber("WO-DUPLICATE")).thenReturn(true);

        WorkOrderRequest request = WorkOrderRequest.builder()
                .workOrderNumber("WO-DUPLICATE")
                .assetId(1L)
                .maintenanceTypeId(1L)
                .priority(Priority.LOW)
                .status(WorkOrderStatus.OPEN)
                .build();

        assertThrows(DuplicateResourceException.class, () -> {
            workOrderService.createWorkOrder(request);
        });
    }

    @Test
    @DisplayName("Rule 2: CRITICAL work order must have an assigned technician")
    void testRule2_CriticalRequiresTechnician() {
        when(workOrderRepository.existsByWorkOrderNumber("WO-CRIT-01")).thenReturn(false);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(maintenanceTypeRepository.findById(1L)).thenReturn(Optional.of(regularType));

        WorkOrderRequest request = WorkOrderRequest.builder()
                .workOrderNumber("WO-CRIT-01")
                .assetId(1L)
                .maintenanceTypeId(1L)
                .priority(Priority.CRITICAL)
                .assignedTechnicianId(null)
                .status(WorkOrderStatus.OPEN)
                .build();

        assertThrows(BusinessRuleException.class, () -> {
            workOrderService.createWorkOrder(request);
        });
    }

    @Test
    @DisplayName("Rule 3 & 4: COMPLETED or CANCELLED work order cannot make illegal transitions")
    void testRule3And4_TerminalTransitions() {
        WorkOrder completedOrder = WorkOrder.builder()
                .id(10L)
                .workOrderNumber("WO-COMPLETED")
                .status(WorkOrderStatus.COMPLETED)
                .asset(sampleAsset)
                .maintenanceType(regularType)
                .priority(Priority.LOW)
                .build();

        when(workOrderRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(completedOrder));

        WorkOrderRequest updateToOpen = WorkOrderRequest.builder()
                .workOrderNumber("WO-COMPLETED")
                .assetId(1L)
                .maintenanceTypeId(1L)
                .priority(Priority.LOW)
                .status(WorkOrderStatus.OPEN)
                .build();

        assertThrows(BusinessRuleException.class, () -> {
            workOrderService.updateWorkOrder(10L, updateToOpen);
        });
    }

    @Test
    @DisplayName("Rule 5: Newly created work order cannot have scheduled date in the past")
    void testRule5_PastScheduledDate() {
        when(workOrderRepository.existsByWorkOrderNumber("WO-PAST-01")).thenReturn(false);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(maintenanceTypeRepository.findById(1L)).thenReturn(Optional.of(regularType));

        WorkOrderRequest request = WorkOrderRequest.builder()
                .workOrderNumber("WO-PAST-01")
                .assetId(1L)
                .maintenanceTypeId(1L)
                .priority(Priority.MEDIUM)
                .scheduledDate(LocalDate.now().minusDays(2))
                .status(WorkOrderStatus.OPEN)
                .build();

        assertThrows(BusinessRuleException.class, () -> {
            workOrderService.createWorkOrder(request);
        });
    }

    @Test
    @DisplayName("Rule 6: OUT_OF_SERVICE asset cannot receive work order unless emergency")
    void testRule6_OutOfServiceAssetRequiresEmergency() {
        sampleAsset.setStatus(AssetStatus.OUT_OF_SERVICE);

        when(workOrderRepository.existsByWorkOrderNumber("WO-OOS-01")).thenReturn(false);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(sampleAsset));
        when(maintenanceTypeRepository.findById(1L)).thenReturn(Optional.of(regularType));

        WorkOrderRequest nonEmergencyRequest = WorkOrderRequest.builder()
                .workOrderNumber("WO-OOS-01")
                .assetId(1L)
                .maintenanceTypeId(1L)
                .priority(Priority.HIGH)
                .status(WorkOrderStatus.OPEN)
                .build();

        assertThrows(BusinessRuleException.class, () -> {
            workOrderService.createWorkOrder(nonEmergencyRequest);
        });
    }
}
