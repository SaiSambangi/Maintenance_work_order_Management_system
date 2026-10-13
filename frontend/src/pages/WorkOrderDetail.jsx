/**
 * WorkOrderDetail.jsx — Screen 3: Full details of one work order.
 *
 * What this page does:
 * 1. Gets the work order ID from the URL (e.g., /orders/5 → id = 5)
 * 2. Loads the full work order details from the backend
 * 3. Displays all information: asset, technician, maintenance type, dates, etc.
 * 4. Allows valid status transitions via dropdown
 * 5. Shows the maintenance history audit trail
 */

import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { workOrderApi, technicianApi } from '../services/api'
import './WorkOrderDetail.css'

/**
 * Maps each current status to the statuses it can transition to.
 * This mirrors the business rules defined in the backend.
 */
const ALLOWED_TRANSITIONS = {
  OPEN:        ['ASSIGNED', 'CANCELLED'],
  ASSIGNED:    ['IN_PROGRESS', 'CANCELLED'],
  IN_PROGRESS: ['COMPLETED'],
  COMPLETED:   [],  // Terminal state
  CANCELLED:   [],  // Terminal state
}

function WorkOrderDetail() {
  // useParams extracts URL parameters — :id from /orders/:id
  const { id } = useParams()
  const navigate = useNavigate()

  // --- State ---
  const [workOrder, setWorkOrder] = useState(null)
  const [history, setHistory] = useState([])
  const [technicians, setTechnicians] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // Status update state
  const [newStatus, setNewStatus] = useState('')
  const [newTechnicianId, setNewTechnicianId] = useState('')
  const [updating, setUpdating] = useState(false)
  const [updateSuccess, setUpdateSuccess] = useState('')
  const [updateError, setUpdateError] = useState('')

  /** Load the work order and its history */
  const loadWorkOrder = async () => {
    setLoading(true)
    setError('')
    try {
      const [woRes, historyRes] = await Promise.all([
        workOrderApi.getById(id),
        workOrderApi.getHistory(id),
      ])
      const wo = woRes.data
      setWorkOrder(wo)
      setHistory(historyRes.data)
      // Pre-populate the status selector with the current status
      setNewStatus(wo.status)
      setNewTechnicianId(wo.assignedTechnician?.id?.toString() || '')
    } catch (err) {
      setError('Failed to load work order details.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadWorkOrder()
  }, [id])

  /** Load technicians for the assignment dropdown */
  useEffect(() => {
    technicianApi.getAll()
      .then(res => setTechnicians(res.data))
      .catch(err => console.error('Failed to load technicians:', err))
  }, [])

  /**
   * Handle the status update form submission.
   *
   * We need to send the FULL work order data (not just the status)
   * because our PUT endpoint expects the complete work order request body.
   */
  const handleUpdate = async () => {
    setUpdating(true)
    setUpdateError('')
    setUpdateSuccess('')

    try {
      const updateBody = {
        workOrderNumber: workOrder.workOrderNumber,
        assetId: workOrder.asset.id,
        maintenanceTypeId: workOrder.maintenanceType.id,
        description: workOrder.description,
        priority: workOrder.priority,
        assignedTechnicianId: newTechnicianId ? Number(newTechnicianId) : null,
        scheduledDate: workOrder.scheduledDate,
        status: newStatus,
      }

      await workOrderApi.update(id, updateBody)
      setUpdateSuccess('✅ Work order updated successfully!')

      // Reload the work order to show updated data
      await loadWorkOrder()
    } catch (err) {
      const backendMessage = err.response?.data?.message
        || 'Failed to update work order.'
      setUpdateError(`❌ ${backendMessage}`)
      console.error(err)
    } finally {
      setUpdating(false)
    }
  }

  /** Format a date string for display */
  const formatDate = (dateStr) => {
    if (!dateStr) return '—'
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric', month: 'long', day: 'numeric'
    })
  }

  /** Format a datetime string for display */
  const formatDateTime = (dateStr) => {
    if (!dateStr) return '—'
    return new Date(dateStr).toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    })
  }

  /** Compute allowed next statuses based on the current status */
  const allowedNextStatuses = workOrder
    ? ALLOWED_TRANSITIONS[workOrder.status] || []
    : []

  const isTerminalStatus = allowedNextStatuses.length === 0

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="page-container">
        <div className="alert alert-error">{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/')}>
          ← Back to List
        </button>
      </div>
    )
  }

  if (!workOrder) return null

  return (
    <div className="page-container">

      {/* Header */}
      <div className="page-header">
        <div>
          <button className="btn btn-secondary back-btn" onClick={() => navigate('/')}>
            ← Back to List
          </button>
          <h2 className="page-title" style={{ marginTop: '8px' }}>
            🔧 {workOrder.workOrderNumber}
          </h2>
        </div>
        <div className="header-badges">
          <span className={`badge badge-${workOrder.priority}`} style={{ fontSize: '14px', padding: '4px 12px' }}>
            {workOrder.priority}
          </span>
          <span className={`badge badge-${workOrder.status}`} style={{ fontSize: '14px', padding: '4px 12px' }}>
            {workOrder.status?.replace('_', ' ')}
          </span>
        </div>
      </div>

      {/* Success / Error messages for updates */}
      {updateSuccess && <div className="alert alert-success">{updateSuccess}</div>}
      {updateError && <div className="alert alert-error">{updateError}</div>}

      <div className="detail-grid">

        {/* ---- Left Column: Main Details ---- */}
        <div className="detail-left">

          {/* Asset Information */}
          <div className="card detail-card">
            <h3 className="detail-card-title">🏭 Asset Information</h3>
            <div className="detail-rows">
              <div className="detail-row">
                <span className="detail-label">Asset ID</span>
                <span className="detail-value">{workOrder.asset?.assetId}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Type</span>
                <span className="detail-value">{workOrder.asset?.assetType}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Manufacturer</span>
                <span className="detail-value">{workOrder.asset?.manufacturer || '—'}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Model</span>
                <span className="detail-value">{workOrder.asset?.model || '—'}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Serial Number</span>
                <span className="detail-value">{workOrder.asset?.serialNumber || '—'}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Asset Status</span>
                <span className={`badge badge-${workOrder.asset?.status?.replace('_', '')}`}>
                  {workOrder.asset?.status?.replace('_', ' ')}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Usage Hours</span>
                <span className="detail-value">{workOrder.asset?.usageHours ?? '—'}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Usage Cycles</span>
                <span className="detail-value">{workOrder.asset?.usageCycles ?? '—'}</span>
              </div>
            </div>
          </div>

          {/* Maintenance Information */}
          <div className="card detail-card">
            <h3 className="detail-card-title">🔨 Maintenance Information</h3>
            <div className="detail-rows">
              <div className="detail-row">
                <span className="detail-label">Maintenance Type</span>
                <span className="detail-value">
                  {workOrder.maintenanceType?.name}
                  {workOrder.maintenanceType?.isEmergency && (
                    <span className="emergency-badge"> 🚨 Emergency</span>
                  )}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Priority</span>
                <span className={`badge badge-${workOrder.priority}`}>{workOrder.priority}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Scheduled Date</span>
                <span className="detail-value">{formatDate(workOrder.scheduledDate)}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Created Date</span>
                <span className="detail-value">{formatDateTime(workOrder.createdDate)}</span>
              </div>
              {workOrder.completedDate && (
                <div className="detail-row">
                  <span className="detail-label">Completed Date</span>
                  <span className="detail-value">{formatDateTime(workOrder.completedDate)}</span>
                </div>
              )}
              {workOrder.description && (
                <div className="detail-row description-row">
                  <span className="detail-label">Description</span>
                  <p className="detail-description">{workOrder.description}</p>
                </div>
              )}
            </div>
          </div>

          {/* Technician Information */}
          <div className="card detail-card">
            <h3 className="detail-card-title">👤 Assigned Technician</h3>
            {workOrder.assignedTechnician ? (
              <div className="detail-rows">
                <div className="detail-row">
                  <span className="detail-label">Name</span>
                  <span className="detail-value">{workOrder.assignedTechnician.name}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Skill</span>
                  <span className="detail-value">{workOrder.assignedTechnician.skill || '—'}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Status</span>
                  <span className="detail-value">{workOrder.assignedTechnician.status}</span>
                </div>
              </div>
            ) : (
              <p className="unassigned-message">⚠️ No technician assigned yet.</p>
            )}
          </div>

        </div>

        {/* ---- Right Column: Status Update + History ---- */}
        <div className="detail-right">

          {/* Status Update Panel */}
          <div className="card detail-card">
            <h3 className="detail-card-title">🔄 Update Status</h3>

            {isTerminalStatus ? (
              <div className="alert alert-info">
                This work order is in a terminal state ({workOrder.status}) and cannot be changed.
              </div>
            ) : (
              <div>
                <p className="transition-hint">
                  Current: <strong>{workOrder.status?.replace('_', ' ')}</strong><br/>
                  Can transition to: <strong>{allowedNextStatuses.join(', ')}</strong>
                </p>

                {/* New status selector */}
                <div className="form-group">
                  <label className="form-label">New Status</label>
                  <select
                    className="form-control"
                    value={newStatus}
                    onChange={(e) => setNewStatus(e.target.value)}
                  >
                    <option value={workOrder.status}>{workOrder.status} (current)</option>
                    {allowedNextStatuses.map(s => (
                      <option key={s} value={s}>{s.replace('_', ' ')}</option>
                    ))}
                  </select>
                </div>

                {/* Technician selector (shown when transitioning to ASSIGNED) */}
                {(newStatus === 'ASSIGNED' || newStatus === 'IN_PROGRESS') && (
                  <div className="form-group">
                    <label className="form-label">Assign Technician</label>
                    <select
                      className="form-control"
                      value={newTechnicianId}
                      onChange={(e) => setNewTechnicianId(e.target.value)}
                    >
                      <option value="">— Unassigned —</option>
                      {technicians.map(tech => (
                        <option key={tech.id} value={tech.id}>
                          {tech.name} ({tech.skill}) — {tech.status}
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <button
                  className="btn btn-primary"
                  style={{ width: '100%', justifyContent: 'center' }}
                  onClick={handleUpdate}
                  disabled={updating || newStatus === workOrder.status}
                >
                  {updating ? '⏳ Updating...' : '💾 Save Changes'}
                </button>
              </div>
            )}
          </div>

          {/* Maintenance History */}
          <div className="card detail-card">
            <h3 className="detail-card-title">📜 Maintenance History</h3>
            {history.length === 0 ? (
              <p style={{ color: '#9ca3af', fontSize: '13px' }}>No history recorded yet.</p>
            ) : (
              <div className="history-list">
                {history.map(record => (
                  <div key={record.id} className="history-item">
                    <div className="history-arrow">
                      <span className={`badge badge-${record.previousStatus || 'OPEN'}`}>
                        {record.previousStatus || 'NEW'}
                      </span>
                      <span className="arrow">→</span>
                      <span className={`badge badge-${record.newStatus}`}>
                        {record.newStatus?.replace('_', ' ')}
                      </span>
                    </div>
                    {record.notes && (
                      <p className="history-notes">{record.notes}</p>
                    )}
                    <p className="history-time">🕒 {formatDateTime(record.changedAt)}</p>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      </div>
    </div>
  )
}

export default WorkOrderDetail
