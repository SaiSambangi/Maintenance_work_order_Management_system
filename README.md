# Maintenance Work Order Management System

A full-stack web application designed for industrial/facility asset tracking, technician management, and maintenance work order lifecycles with backend business rule enforcement.

---

## 1. Technology Stack

- **Frontend:** React.js, React Hooks, React Router 7, Axios, CSS3, HTML5, Vite
- **Backend:** Java 17, Spring Boot 3.2.5, Spring Web (REST), Spring Data JPA, Hibernate, Bean Validation, Lombok
- **Database:** MySQL 8.0+ (Relational schema with Foreign Keys, Unique constraints, and Indexes)
- **Build Tools:** Apache Maven (Wrapper included), Node.js / npm

---

## 2. Architecture & Directory Structure

```text
Maintenance_work_order_Management_system/
├── backend/
│   ├── src/main/java/com/maintenance/workorder/
│   │   ├── config/            # CORS configuration, Initial data seeder (DataInitializer)
│   │   ├── controller/        # REST Controllers (Asset, Technician, MaintenanceType, WorkOrder)
│   │   ├── dto/               # Data Transfer Objects (Requests and Responses)
│   │   ├── entity/            # JPA Entities (Asset, Technician, MaintenanceType, WorkOrder, History)
│   │   ├── exception/         # Custom exceptions & GlobalExceptionHandler
│   │   ├── repository/        # Spring Data JPA Repositories & Specifications
│   │   ├── service/           # Business Logic layer & Status Transition validation
│   │   └── WorkorderApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── src/test/java/         # Unit and Integration test suites (H2 / Mockito)
│   ├── schema.sql             # Complete MySQL creation and seed script
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/        # Reusable UI components (Navbar, alerts, badges)
│   │   ├── pages/             # 3 Core Screens (WorkOrderList, CreateWorkOrder, WorkOrderDetail)
│   │   ├── services/          # Centralized Axios API client (api.js)
│   │   ├── App.jsx            # React Router route hierarchy
│   │   ├── main.jsx           # Entry mount
│   │   └── index.css          # Global styling & layout system
│   └── package.json
├── .env.example
├── README.md
└── AI_USAGE.md
```

---

## 3. Database Setup (MySQL) & Environment Variables

1. Make sure your MySQL 8.0+ server is running.
2. Initialize the database schema if needed:
   ```sql
   CREATE DATABASE IF NOT EXISTS maintenance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   *(Hibernate will auto-create or update tables upon boot, and `DataInitializer` will seed sample assets, technicians, and maintenance types automatically).*

3. **Configure Environment Variables (Safe for GitHub):**
   `application.properties` does **not** contain hardcoded passwords. Instead, it reads from environment variables:
   - `DB_PASSWORD` or `SPRING_DATASOURCE_PASSWORD`
   - `DB_USER` or `SPRING_DATASOURCE_USERNAME` (defaults to `root`)
   - `DB_URL` or `SPRING_DATASOURCE_URL` (defaults to local `maintenance_db`)

   **Option A: Create a local `.env` file (Recommended for development)**
   Copy `.env.example` to `.env` in the root folder:
   ```cmd
   copy .env.example .env
   ```
   Open `.env` and set your real MySQL password:
   ```env
   DB_USER=root
   DB_PASSWORD=YOUR_ACTUAL_MYSQL_PASSWORD
   ```
   *(Note: `.env` is already included in `.gitignore` and will never be pushed to GitHub).*

   **Option B: Set environment variables in Windows PowerShell**
   ```powershell
   $env:DB_USER = "root"
   $env:DB_PASSWORD = "YOUR_ACTUAL_MYSQL_PASSWORD"
   ```

   **Option C: Set environment variables in Windows Command Prompt (cmd.exe)**
   ```cmd
   set DB_USER=root
   set DB_PASSWORD=YOUR_ACTUAL_MYSQL_PASSWORD
   ```

---

## 4. How to Run the Backend

1. Navigate to the `backend` folder:
   ```bash
   cd backend
   ```
2. Set `JAVA_HOME` to JDK 17 (if not already set in system environment variables):
   - **Windows PowerShell:**
     ```powershell
     $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
     ```
   - **Linux / macOS:**
     ```bash
     export JAVA_HOME=/path/to/jdk-17
     ```
3. Run the application:
   - **Windows:**
     ```cmd
     mvnw.cmd spring-boot:run
     ```
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   *(Or with global maven: `mvn spring-boot:run`)*

The backend will start at: `http://localhost:8080`.

To run test suites:
```powershell
mvnw.cmd test
```

---

## 5. How to Run the Frontend

1. Navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open your browser and navigate to:
   ```
   http://localhost:5173
   ```

---

## 6. Business Rules Enforced

1. **Unique Work Order Number:** Checked on creation and updates (`DuplicateResourceException` -> HTTP 409).
2. **CRITICAL Priority Technician Requirement:** CRITICAL work orders must have an assigned technician (`BusinessRuleException` -> HTTP 400).
3. **COMPLETED Status Lock:** COMPLETED work orders cannot transition back to OPEN or any other state.
4. **CANCELLED Status Lock:** CANCELLED work orders cannot transition to IN_PROGRESS.
5. **Future Scheduled Date:** Newly scheduled work orders cannot have dates in the past.
6. **OUT_OF_SERVICE Asset Rule:** Assets with status `OUT_OF_SERVICE` can only receive work orders with an `is_emergency=true` maintenance type.

---

## 7. API Endpoints

### Assets
- `POST /api/assets` - Register asset
- `GET /api/assets` - List all assets
- `GET /api/assets/{id}` - Retrieve asset by ID
- `PUT /api/assets/{id}` - Update asset

### Technicians
- `POST /api/technicians` - Register technician
- `GET /api/technicians` - List all technicians
- `GET /api/technicians/{id}` - Retrieve technician by ID

### Maintenance Types
- `GET /api/maintenance-types` - List maintenance types (includes emergency indicator)

### Work Orders
- `POST /api/work-orders` - Create work order
- `GET /api/work-orders` - List work orders (supports filters: `?status=`, `?priority=`, `?asset_id=`, `?technician_id=`)
- `GET /api/work-orders/{id}` - Retrieve full work order details
- `PUT /api/work-orders/{id}` - Update work order / transition status
- `GET /api/work-orders/{id}/history` - Retrieve audit trail of status changes
