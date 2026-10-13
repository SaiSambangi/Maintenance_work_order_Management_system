-- =====================================================================
-- Maintenance Work Order Management System Database Schema & Initial Data
-- MySQL 8.0+
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `maintenance_db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `maintenance_db`;

-- Drop existing tables if needed (in reverse dependency order)
DROP TABLE IF EXISTS `maintenance_history`;
DROP TABLE IF EXISTS `work_orders`;
DROP TABLE IF EXISTS `maintenance_types`;
DROP TABLE IF EXISTS `technicians`;
DROP TABLE IF EXISTS `assets`;

-- ---------------------------------------------------------------------
-- 1. Table: assets
-- ---------------------------------------------------------------------
CREATE TABLE `assets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `asset_id` VARCHAR(100) NOT NULL,
  `asset_type` VARCHAR(100) NOT NULL,
  `manufacturer` VARCHAR(100) DEFAULT NULL,
  `model` VARCHAR(100) DEFAULT NULL,
  `serial_number` VARCHAR(100) DEFAULT NULL,
  `usage_hours` INT DEFAULT 0,
  `usage_cycles` INT DEFAULT 0,
  `status` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assets_asset_id` (`asset_id`),
  UNIQUE KEY `uk_assets_serial_number` (`serial_number`),
  KEY `idx_assets_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. Table: technicians
-- ---------------------------------------------------------------------
CREATE TABLE `technicians` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  `skill` VARCHAR(100) DEFAULT NULL,
  `status` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_technicians_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. Table: maintenance_types
-- ---------------------------------------------------------------------
CREATE TABLE `maintenance_types` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) DEFAULT NULL,
  `is_emergency` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_maintenance_types_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. Table: work_orders
-- ---------------------------------------------------------------------
CREATE TABLE `work_orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `work_order_number` VARCHAR(100) NOT NULL,
  `asset_id` BIGINT NOT NULL,
  `maintenance_type_id` BIGINT NOT NULL,
  `description` VARCHAR(2000) DEFAULT NULL,
  `priority` VARCHAR(20) NOT NULL,
  `technician_id` BIGINT DEFAULT NULL,
  `scheduled_date` DATE DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_orders_number` (`work_order_number`),
  KEY `idx_wo_status` (`status`),
  KEY `idx_wo_priority` (`priority`),
  KEY `idx_wo_asset_id` (`asset_id`),
  KEY `idx_wo_technician_id` (`technician_id`),
  CONSTRAINT `fk_wo_asset` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_wo_maint_type` FOREIGN KEY (`maintenance_type_id`) REFERENCES `maintenance_types` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_wo_technician` FOREIGN KEY (`technician_id`) REFERENCES `technicians` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. Table: maintenance_history
-- ---------------------------------------------------------------------
CREATE TABLE `maintenance_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `work_order_id` BIGINT NOT NULL,
  `previous_status` VARCHAR(20) DEFAULT NULL,
  `new_status` VARCHAR(20) NOT NULL,
  `notes` VARCHAR(1000) DEFAULT NULL,
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_mh_work_order_id` (`work_order_id`),
  KEY `idx_mh_changed_at` (`changed_at`),
  CONSTRAINT `fk_mh_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- Sample Seed Data
-- =====================================================================

INSERT INTO `maintenance_types` (`name`, `description`, `is_emergency`) VALUES
('Preventive Maintenance', 'Scheduled routine maintenance to prevent equipment failures', FALSE),
('Corrective Repair', 'Fixing identified faults or functional degradation', FALSE),
('Emergency Repair', 'Critical failure repairs allowed for OUT_OF_SERVICE assets', TRUE),
('Safety Inspection', 'Comprehensive safety compliance check', FALSE),
('Calibration & Tuning', 'Instrument recalibration and fine tuning', FALSE);

INSERT INTO `assets` (`asset_id`, `asset_type`, `manufacturer`, `model`, `serial_number`, `usage_hours`, `usage_cycles`, `status`) VALUES
('ASSET-001', 'HVAC System', 'Carrier', '50XC-060', 'SN-HVAC-001', 4500, 900, 'ACTIVE'),
('ASSET-002', 'Diesel Generator', 'Caterpillar', 'C15', 'SN-GEN-002', 12000, 2400, 'ACTIVE'),
('ASSET-003', 'Water Pump', 'Grundfos', 'CM5-5', 'SN-PUMP-003', 8200, 1640, 'UNDER_MAINTENANCE'),
('ASSET-004', 'Passenger Elevator', 'Otis', 'Gen2-MR', 'SN-ELEV-004', 22000, 110000, 'ACTIVE'),
('ASSET-005', 'Air Compressor', 'Atlas Copco', 'GA30', 'SN-COMP-005', 5500, 1100, 'OUT_OF_SERVICE');

INSERT INTO `technicians` (`name`, `skill`, `status`) VALUES
('Alice Johnson', 'Electrical', 'AVAILABLE'),
('Bob Martinez', 'Mechanical', 'AVAILABLE'),
('Carol Chen', 'HVAC', 'BUSY'),
('David Kim', 'Plumbing', 'AVAILABLE'),
('Eva Patel', 'Electronics', 'ON_LEAVE');
