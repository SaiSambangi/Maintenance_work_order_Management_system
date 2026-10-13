/**
 * WorkOrderList.jsx — Screen 1: Display all work orders with search and filters.
 *
 * What this page does:
 * 1. Loads all work orders from the backend when the page opens
 * 2. Lets you filter by status, priority, asset, and technician
 * 3. Shows results in a table
 * 4. Each row is clickable to view details
 * 5. Has a "Create Work Order" button
 *
 * React Hooks used:
 *   - useState: stores data that can change (like the list of work orders)
 *   - useEffect: runs code when the component loads or when a value changes
 */

import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { workOrderApi, assetApi, technicianApi } from '../services/api'
import './WorkOrderList.css'

// These are the possible values for our dropdown filters.
// They match the enum values defined in the backend.
const STATUS_OPTIONS = ['', 'OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']
const PRIORITY_OPTIONS = ['', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

function WorkOrderList() {
  // useNavigate lets us change pages programmatically (e.g., navigate to detail page)
  const navigate = useNavigate()

  // --- State variables (things that can change and cause re-render) ---

  /** The full list of work orders returned by the backend */
  const [workOrders, setWorkOrders] = useState([])

  /** All assets (for the asset filter dropdown) */
  const [assets, setAssets] = useState([])

  /** All technicians (for the technician filter dropdown) */
  const [technicians, setTechnicians] = useState([])

  /** Is the data currently loading? Used to show a spinner */
  const [loading, setLoading] = useState(true)

  /** Error message to show if the API call fails */
  const [error, setError] = useState('')

  /** Text the user types in the search box */
  const [searchText, setSearchText] = useState('')

  /** Current filter values — these are sent to the backend as query params */
  const [filters, setFilters] = useState({
    status: '',
    priority: '',
    asset_id: '',
    technician_id: '',
  })

  /**
   * Loads work orders from the backend.
   *
   * useCallback memoizes this function so React doesn't recreate it on every render.
   * The filters array means: "recreate this function whenever filters change".
   */
  const loadWorkOrders = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const response = await workOrderApi.getAll(filters)
      setWorkOrders(response.data)
    } catch (err) {
      setError('Failed to load work orders. Is the backend running?')
      console.error(err)
    } finally {
      // Always set loading to false, whether success or error
      setLoading(false)
    }
  }, [filters])

  /**
   * useEffect runs code after the component renders.
   * This one loads work orders whenever the filters change.
   */
  useEffect(() => {
    loadWorkOrders()
  }, [loadWorkOrders])

  /**
   * Load assets and technicians once (for the filter dropdowns).
   * The empty [] means: "only run once when the component first loads".
   */
  useEffect(() => {
    const loadDropdownData = async () => {
      try {
        const [assetsRes, techsRes] = await Promise.all([
          assetApi.getAll(),
          technicianApi.getAll(),
        ])
        setAssets(assetsRes.data)
        setTechnicians(techsRes.data)
      } catch (err) {
        console.error('Failed to load dropdown data:', err)
      }
    }
    loadDropdownData()
  }, [])

  /**
   * When a filter dropdown changes, update the filters state.
   * The spread operator (...filters) keeps all existing filters
   * and just updates the one that changed.
   */
  const handleFilterChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }))
  }

  /** Clear all filters and show all work orders */
  const handleClearFilters = () => {
    setFilters({ status: '', priority: '', asset_id: '', technician_id: '' })
    setSearchText('')
  }

  /**
   * Client-side search — filter the work orders by the search text.
   * This searches across work order number, asset name, and maintenance type.
   * Note: The filter dropdowns use the backend API, but text search is done here
   * in the browser for a better user experience.
   */
  const filteredOrders = workOrders.filter(order => {
    if (!searchText) return true
    const lowerSearch = searchText.toLowerCase()
    return (
      order.workOrderNumber?.toLowerCase().includes(lowerSearch) ||
      order.asset?.assetId?.toLowerCase().includes(lowerSearch) ||
      order.maintenanceType?.name?.toLowerCase().includes(lowerSearch) ||
      order.assignedTechnician?.name?.toLowerCase().includes(lowerSearch)
    )
  })

  /** Navigate to the detail page when a row is clicked */
  const handleRowClick = (orderId) => {
    navigate(`/orders/${orderId}`)
  }

  return (
    <div className="page-container">
      {/* Page header with title and Create button */}
      <div className="page-header">
        <h2 className="page-title">📋 Work Orders</h2>
        <button className="btn btn-primary" onClick={() => navigate('/create')}>
          ➕ Create Work Order
        </button>
      </div>

      {/* Error message */}
      {error && <div className="alert alert-error">{error}</div>}

      {/* Search and filters panel */}
      <div className="card filters-card">
        <div className="filters-row">
          {/* Text search */}
          <div className="search-box">
            <span className="search-icon">🔍</span>
            <input
              type="text"
              placeholder="Search by WO number, asset, type, technician..."
              className="form-control search-input"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
            />
          </div>

          {/* Status filter */}
          <select
            className="form-control filter-select"
            value={filters.status}
            onChange={(e) => handleFilterChange('status', e.target.value)}
          >
            <option value="">All Statuses</option>
            {STATUS_OPTIONS.filter(s => s).map(s => (
              <option key={s} value={s}>{s.replace('_', ' ')}</option>
            ))}
          </select>

          {/* Priority filter */}
          <select
            className="form-control filter-select"
            value={filters.priority}
            onChange={(e) => handleFilterChange('priority', e.target.value)}
          >
            <option value="">All Priorities</option>
            {PRIORITY_OPTIONS.filter(p => p).map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>

          {/* Asset filter */}
          <select
            className="form-control filter-select"
            value={filters.asset_id}
            onChange={(e) => handleFilterChange('asset_id', e.target.value)}
          >
            <option value="">All Assets</option>
            {assets.map(asset => (
              <option key={asset.id} value={asset.id}>
                {asset.assetId} - {asset.assetType}
              </option>
            ))}
          </select>

          {/* Technician filter */}
          <select
            className="form-control filter-select"
            value={filters.technician_id}
            onChange={(e) => handleFilterChange('technician_id', e.target.value)}
          >
            <option value="">All Technicians</option>
            {technicians.map(tech => (
              <option key={tech.id} value={tech.id}>{tech.name}</option>
            ))}
          </select>

          {/* Clear filters button */}
          <button className="btn btn-secondary" onClick={handleClearFilters}>
            ✕ Clear
          </button>
        </div>
      </div>

      {/* Results count */}
      <div className="results-info">
        Showing <strong>{filteredOrders.length}</strong> work order{filteredOrders.length !== 1 ? 's' : ''}
        {(Object.values(filters).some(f => f) || searchText) && (
          <span> (filtered)</span>
        )}
      </div>

      {/* Work orders table */}
      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
        </div>
      ) : filteredOrders.length === 0 ? (
        <div className="card empty-state">
          <div style={{ fontSize: '48px', marginBottom: '12px' }}>📭</div>
          <h3>No work orders found</h3>
          <p>Try adjusting your filters or create a new work order.</p>
          <button className="btn btn-primary" style={{ marginTop: '16px' }}
            onClick={() => navigate('/create')}>
            Create First Work Order
          </button>
        </div>
      ) : (
        <div className="card" style={{ padding: 0 }}>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>WO Number</th>
                  <th>Asset</th>
                  <th>Maintenance Type</th>
                  <th>Priority</th>
                  <th>Technician</th>
                  <th>Scheduled Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {filteredOrders.map(order => (
                  <tr
                    key={order.id}
                    className="clickable"
                    onClick={() => handleRowClick(order.id)}
                    title="Click to view details"
                  >
                    <td>
                      <span className="wo-number">{order.workOrderNumber}</span>
                    </td>
                    <td>
                      <div className="asset-info">
                        <span className="asset-id">{order.asset?.assetId}</span>
                        <span className="asset-type">{order.asset?.assetType}</span>
                      </div>
                    </td>
                    <td>{order.maintenanceType?.name}</td>
                    <td>
                      <span className={`badge badge-${order.priority}`}>
                        {order.priority}
                      </span>
                    </td>
                    <td>
                      {order.assignedTechnician
                        ? <span className="technician-name">👤 {order.assignedTechnician.name}</span>
                        : <span className="unassigned">— Unassigned</span>
                      }
                    </td>
                    <td>
                      {order.scheduledDate
                        ? new Date(order.scheduledDate).toLocaleDateString()
                        : '—'
                      }
                    </td>
                    <td>
                      <span className={`badge badge-${order.status}`}>
                        {order.status?.replace('_', ' ')}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

export default WorkOrderList
