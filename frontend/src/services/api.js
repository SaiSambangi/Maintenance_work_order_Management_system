/**
 * api.js — Centralized API service for all backend calls.
 *
 * Think of this file as the "phone book" for our backend.
 * Instead of writing the URL "http://localhost:8080/api/..." everywhere,
 * we define it once here. If the backend URL ever changes, we only
 * need to update this one file.
 *
 * We use Axios (a popular HTTP client) instead of the raw Fetch API
 * because Axios automatically parses JSON and has better error handling.
 */

import axios from 'axios';

// The base URL for all API calls. Change this if your backend runs elsewhere.
const BASE_URL = 'http://localhost:8080/api';

// Create an Axios "instance" with our base URL pre-configured.
// Every call we make through this instance will automatically prepend this URL.
const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ============================================================
//  ASSET APIs
// ============================================================

export const assetApi = {
  /** GET /api/assets — Get all assets */
  getAll: () => api.get('/assets'),

  /** GET /api/assets/:id — Get one asset by ID */
  getById: (id) => api.get(`/assets/${id}`),

  /** POST /api/assets — Create a new asset */
  create: (data) => api.post('/assets', data),

  /** PUT /api/assets/:id — Update an asset */
  update: (id, data) => api.put(`/assets/${id}`, data),
};

// ============================================================
//  TECHNICIAN APIs
// ============================================================

export const technicianApi = {
  /** GET /api/technicians — Get all technicians */
  getAll: () => api.get('/technicians'),

  /** GET /api/technicians/:id — Get one technician */
  getById: (id) => api.get(`/technicians/${id}`),

  /** POST /api/technicians — Register a new technician */
  create: (data) => api.post('/technicians', data),
};

// ============================================================
//  MAINTENANCE TYPE APIs
// ============================================================

export const maintenanceTypeApi = {
  /** GET /api/maintenance-types — Get all maintenance types */
  getAll: () => api.get('/maintenance-types'),
};

// ============================================================
//  WORK ORDER APIs
// ============================================================

export const workOrderApi = {
  /**
   * GET /api/work-orders — Get all work orders.
   *
   * The 'filters' parameter is an object like:
   *   { status: 'OPEN', priority: 'HIGH', asset_id: 1 }
   *
   * Axios automatically converts this to URL query parameters:
   *   /api/work-orders?status=OPEN&priority=HIGH&asset_id=1
   */
  getAll: (filters = {}) => {
    // Remove any empty/null/undefined filter values so we don't
    // send "?status=&priority=" to the backend
    const cleanFilters = {};
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== '' && value !== null && value !== undefined) {
        cleanFilters[key] = value;
      }
    });
    return api.get('/work-orders', { params: cleanFilters });
  },

  /** GET /api/work-orders/:id — Get one work order with full details */
  getById: (id) => api.get(`/work-orders/${id}`),

  /** POST /api/work-orders — Create a new work order */
  create: (data) => api.post('/work-orders', data),

  /** PUT /api/work-orders/:id — Update a work order (including status) */
  update: (id, data) => api.put(`/work-orders/${id}`, data),

  /** GET /api/work-orders/:id/history — Get maintenance history */
  getHistory: (id) => api.get(`/work-orders/${id}/history`),
};

export default api;
