# AI Usage & Technical Decisions Document

## 1. Overview of AI Assistance
This application was developed with AI assistance acting as a Senior Full-Stack Developer and Mentor. The goal was to produce clean, maintainable, enterprise-standard code conforming strictly to the Full-Stack Engineer Assessment requirements.

---

## 2. Technical Decisions & Architectural Choices

### Backend (Spring Boot 3 + Java 17)
1. **Clean Layered Architecture:**
   - **Entity Layer:** Explicit mappings with JPA annotations (`@Entity`, `@Table`, `@Index`, `@ManyToOne`, `@Enumerated`).
   - **DTO Layer:** Isolated request and response contracts (`AssetRequest`, `AssetResponse`, `WorkOrderRequest`, `WorkOrderResponse`, etc.) to prevent entity exposure and mass-assignment vulnerabilities.
   - **Repository Layer:** Extended both `JpaRepository` and `JpaSpecificationExecutor` to allow modular SQL query generation when filtering work orders by multiple parameters simultaneously (`status`, `priority`, `asset_id`, `technician_id`).
   - **Service Layer:** Centralized all 6 business rules and status transition state machines in `WorkOrderService` rather than fragmenting checks in controllers.
   - **Exception Layer:** Standardized REST error payload format with a `@RestControllerAdvice` handling `ResourceNotFoundException` (404), `BusinessRuleException` (400), `DuplicateResourceException` (409), and `MethodArgumentNotValidException` (400).

2. **Automated Seed Mechanism (`DataInitializer`):**
   - Implemented `CommandLineRunner` to seed 5 assets, 5 technicians, and 5 maintenance types (including an emergency type) only when tables are empty, avoiding duplications on restart.

3. **CORS Security Configuration (`CorsConfig`):**
   - Explicitly configured origins for standard development ports (`http://localhost:3000` and `http://localhost:5173`) supporting all CRUD HTTP verbs.

### Frontend (React + Vite + Axios)
1. **Vite Toolchain:**
   - Chosen for lightning-fast HMR and bundle compilation while sticking strictly to React and JavaScript.
2. **Three Core Responsive Screens:**
   - **Screen 1 (`WorkOrderList`):** Instant search filtering (client-side) alongside server-driven multi-parameter filter queries, interactive clickable rows, and responsive tables.
   - **Screen 2 (`CreateWorkOrder`):** Dynamic dropdowns populated from backend endpoints, immediate visual warning when selecting `OUT_OF_SERVICE` assets, past-date calendar restrictions, and descriptive validation error messages.
   - **Screen 3 (`WorkOrderDetail`):** Two-column card dashboard detailing asset technical metrics, technician skills, authorized status progression buttons according to backend state machines, and historical audit entries.
3. **Centralized HTTP Client (`services/api.js`):**
   - Encapsulated Axios configuration and helper routines with clean parameter sanitization.

---

## 3. Issues Identified & Resolutions

| Issue Identified | Root Cause | Resolution |
| :--- | :--- | :--- |
| **Global Maven missing in system PATH** | Maven binaries were not in the Windows environment path. | Downloaded and configured the Apache Maven Wrapper (`.mvn/wrapper` and `mvnw.cmd`) pointing to Maven 3.9.6, enabling seamless cross-platform execution. |
| **H2 Dialect Mismatch during Test Suite** | Spring Boot test context defaulted to MySQL dialect configured in `application.properties`. | Overrode the test profile with H2 dialect and compatible MySQL mode (`MODE=MySQL`) in `@TestPropertySource`. |
| **N+1 Fetching Queries in Work Orders** | Default lazy fetching on `Asset`, `Technician`, and `MaintenanceType` could trigger cascading SQL queries on detail retrieval. | Added `@Query("... LEFT JOIN FETCH ...")` in `WorkOrderRepository.findByIdWithDetails` for single-query loading. |
| **Terminal Status Modifications** | Need to prevent edits once a work order reaches `COMPLETED` or `CANCELLED`. | Added strict status validation checks throwing explicit `BusinessRuleException` messages and disabled action selectors in the React UI. |
