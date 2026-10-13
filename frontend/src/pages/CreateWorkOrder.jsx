/**
 * CreateWorkOrder.jsx — Screen 2: Form to create a new work order.
 *
 * What this form does:
 * 1. Loads assets, technicians, and maintenance types from the backend
 *    (so the user can pick from dropdowns instead of typing IDs)
 * 2. Validates input on the frontend before submitting
 * 3. Sends the form data to POST /api/work-orders
 * 4. Shows success or error messages
 * 5. On success, navigates to the detail page of the new work order
 */

import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { workOrderApi, assetApi, technicianApi, maintenanceTypeApi } from '../services/api'
import './CreateWorkOrder.css'

// Valid status options for creating a work order
const PRIORITY_OPTIONS = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']
const INITIAL_STATUS_OPTIONS = ['OPEN', 'ASSIGNED']

function CreateWorkOrder() {
  const navigate = useNavigate()

  // --- Dropdown data loaded from backend ---
  const [assets, setAssets] = useState([])
  const [technicians, setTechnicians] = useState([])
  const [maintenanceTypes, setMaintenanceTypes] = useState([])
  const [loadingData, setLoadingData] = useState(true)

  // --- Form state ---
  // Each field starts with an empty/default value
  const [formData, setFormData] = useState({
    workOrderNumber: '',
    assetId: '',
    maintenanceTypeId: '',
    description: '',
    priority: 'MEDIUM',
    assignedTechnicianId: '',
    scheduledDate: '',
    status: 'OPEN',
  })

  // --- Form validation errors (shown below each field) ---
  const [errors, setErrors] = useState({})

  // --- Submission state ---
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const [submitSuccess, setSubmitSuccess] = useState('')

  /**
   * Load all dropdown data when the page opens.
   * Promise.all runs all three API calls at the same time (parallel),
   * which is faster than running them one at a time.
   */
  useEffect(() => {
    const loadData = async () => {
      try {
        const [assetsRes, techsRes, typesRes] = await Promise.all([
          assetApi.getAll(),
          technicianApi.getAll(),
          maintenanceTypeApi.getAll(),
        ])
        setAssets(assetsRes.data)
        setTechnicians(techsRes.data)
        setMaintenanceTypes(typesRes.data)
      } catch (err) {
        setSubmitError('Failed to load form data. Please check your backend connection.')
        console.error(err)
      } finally {
        setLoadingData(false)
      }
    }
    loadData()
  }, [])

  /**
   * Handle any form field change.
   *
   * This is a "controlled input" pattern — React tracks every keystroke.
   * The field name comes from the 'name' attribute of the input/select.
   */
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    // Clear the error for this field when the user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  /**
   * Frontend validation — check before sending to backend.
   * Returns true if valid, false if there are errors.
   *
   * Note: Backend validates too. This just gives faster feedback to the user.
   */
  const validate = () => {
    const newErrors = {}

    if (!formData.workOrderNumber.trim()) {
      newErrors.workOrderNumber = 'Work order number is required'
    }
    if (!formData.assetId) {
      newErrors.assetId = 'Please select an asset'
    }
    if (!formData.maintenanceTypeId) {
      newErrors.maintenanceTypeId = 'Please select a maintenance type'
    }
    if (!formData.priority) {
      newErrors.priority = 'Priority is required'
    }
    // Business Rule #2 check on frontend too
    if (formData.priority === 'CRITICAL' && !formData.assignedTechnicianId) {
      newErrors.assignedTechnicianId = 'A technician must be assigned for CRITICAL priority work orders'
    }
    // Business Rule #5: scheduled date cannot be in the past
    if (formData.scheduledDate) {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const scheduled = new Date(formData.scheduledDate)
      if (scheduled < today) {
        newErrors.scheduledDate = 'Scheduled date cannot be in the past'
      }
    }

    setErrors(newErrors)
    // If there are no errors, the object is empty → return true (valid)
    return Object.keys(newErrors).length === 0
  }

  /**
   * Handle form submission.
   */
  const handleSubmit = async (e) => {
    e.preventDefault() // Stop the browser from refreshing the page

    if (!validate()) return // Stop if validation fails

    setSubmitting(true)
    setSubmitError('')
    setSubmitSuccess('')

    try {
      // Build the request body (only include non-empty values)
      const requestBody = {
        workOrderNumber: formData.workOrderNumber,
        assetId: Number(formData.assetId),
        maintenanceTypeId: Number(formData.maintenanceTypeId),
        description: formData.description || null,
        priority: formData.priority,
        assignedTechnicianId: formData.assignedTechnicianId
          ? Number(formData.assignedTechnicianId)
          : null,
        scheduledDate: formData.scheduledDate || null,
        status: formData.status,
      }

      const response = await workOrderApi.create(requestBody)
      setSubmitSuccess(`✅ Work order "${response.data.workOrderNumber}" created successfully!`)

      // Navigate to the detail page after a short delay
      setTimeout(() => {
        navigate(`/orders/${response.data.id}`)
      }, 1500)

    } catch (err) {
      // Show the backend's error message
      const backendMessage = err.response?.data?.message
        || err.response?.data?.error
        || 'Failed to create work order. Please try again.'
      setSubmitError(`❌ ${backendMessage}`)
      console.error(err)
    } finally {
      setSubmitting(false)
    }
  }

  if (loadingData) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h2 className="page-title">➕ Create Work Order</h2>
        <button className="btn btn-secondary" onClick={() => navigate('/')}>
          ← Back to List
        </button>
      </div>

      {/* Success / Error messages */}
      {submitSuccess && <div className="alert alert-success">{submitSuccess}</div>}
      {submitError && <div className="alert alert-error">{submitError}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>

          {/* ---- Section 1: Basic Information ---- */}
          <div className="form-section">
            <h3 className="section-title">📝 Basic Information</h3>
            <div className="grid-2">

              {/* Work Order Number */}
              <div className="form-group">
                <label className="form-label">
                  Work Order Number <span className="required">*</span>
                </label>
                <input
                  type="text"
                  name="workOrderNumber"
                  className={`form-control ${errors.workOrderNumber ? 'error' : ''}`}
                  placeholder="e.g. WO-2024-001"
                  value={formData.workOrderNumber}
                  onChange={handleChange}
                />
                {errors.workOrderNumber && (
                  <span className="error-text">{errors.workOrderNumber}</span>
                )}
              </div>

              {/* Status */}
              <div className="form-group">
                <label className="form-label">
                  Status <span className="required">*</span>
                </label>
                <select
                  name="status"
                  className="form-control"
                  value={formData.status}
                  onChange={handleChange}
                >
                  {INITIAL_STATUS_OPTIONS.map(s => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
              </div>

            </div>
          </div>

          {/* ---- Section 2: Asset & Maintenance Type ---- */}
          <div className="form-section">
            <h3 className="section-title">🏭 Asset & Maintenance</h3>
            <div className="grid-2">

              {/* Asset Dropdown */}
              <div className="form-group">
                <label className="form-label">
                  Asset <span className="required">*</span>
                </label>
                <select
                  name="assetId"
                  className={`form-control ${errors.assetId ? 'error' : ''}`}
                  value={formData.assetId}
                  onChange={handleChange}
                >
                  <option value="">— Select Asset —</option>
                  {assets.map(asset => (
                    <option key={asset.id} value={asset.id}>
                      {asset.assetId} | {asset.assetType}
                      {asset.status === 'OUT_OF_SERVICE' ? ' ⚠️ OUT OF SERVICE' : ''}
                    </option>
                  ))}
                </select>
                {errors.assetId && (
                  <span className="error-text">{errors.assetId}</span>
                )}
                {formData.assetId && assets.find(a => a.id === Number(formData.assetId))?.status === 'OUT_OF_SERVICE' && (
                  <div className="alert alert-error" style={{ marginTop: '6px', padding: '8px 12px', fontSize: '12px' }}>
                    ⚠️ This asset is OUT OF SERVICE. Only emergency maintenance types can be used.
                  </div>
                )}
              </div>

              {/* Maintenance Type Dropdown */}
              <div className="form-group">
                <label className="form-label">
                  Maintenance Type <span className="required">*</span>
                </label>
                <select
                  name="maintenanceTypeId"
                  className={`form-control ${errors.maintenanceTypeId ? 'error' : ''}`}
                  value={formData.maintenanceTypeId}
                  onChange={handleChange}
                >
                  <option value="">— Select Maintenance Type —</option>
                  {maintenanceTypes.map(type => (
                    <option key={type.id} value={type.id}>
                      {type.name} {type.isEmergency ? '🚨 (Emergency)' : ''}
                    </option>
                  ))}
                </select>
                {errors.maintenanceTypeId && (
                  <span className="error-text">{errors.maintenanceTypeId}</span>
                )}
              </div>

            </div>
          </div>

          {/* ---- Section 3: Priority & Technician ---- */}
          <div className="form-section">
            <h3 className="section-title">👤 Priority & Assignment</h3>
            <div className="grid-2">

              {/* Priority Dropdown */}
              <div className="form-group">
                <label className="form-label">
                  Priority <span className="required">*</span>
                </label>
                <select
                  name="priority"
                  className="form-control"
                  value={formData.priority}
                  onChange={handleChange}
                >
                  {PRIORITY_OPTIONS.map(p => (
                    <option key={p} value={p}>{p}</option>
                  ))}
                </select>
                {formData.priority === 'CRITICAL' && (
                  <div className="alert alert-info" style={{ marginTop: '6px', padding: '8px 12px', fontSize: '12px' }}>
                    🚨 CRITICAL priority requires an assigned technician.
                  </div>
                )}
              </div>

              {/* Technician Dropdown */}
              <div className="form-group">
                <label className="form-label">
                  Assigned Technician
                  {formData.priority === 'CRITICAL' && <span className="required"> *</span>}
                </label>
                <select
                  name="assignedTechnicianId"
                  className={`form-control ${errors.assignedTechnicianId ? 'error' : ''}`}
                  value={formData.assignedTechnicianId}
                  onChange={handleChange}
                >
                  <option value="">— Unassigned —</option>
                  {technicians.map(tech => (
                    <option key={tech.id} value={tech.id}>
                      {tech.name} ({tech.skill}) — {tech.status}
                    </option>
                  ))}
                </select>
                {errors.assignedTechnicianId && (
                  <span className="error-text">{errors.assignedTechnicianId}</span>
                )}
              </div>

            </div>
          </div>

          {/* ---- Section 4: Schedule & Description ---- */}
          <div className="form-section">
            <h3 className="section-title">📅 Schedule & Details</h3>

            {/* Scheduled Date */}
            <div className="form-group">
              <label className="form-label">Scheduled Date</label>
              <input
                type="date"
                name="scheduledDate"
                className={`form-control date-input ${errors.scheduledDate ? 'error' : ''}`}
                value={formData.scheduledDate}
                onChange={handleChange}
                min={new Date().toISOString().split('T')[0]}  // Prevent past dates
              />
              {errors.scheduledDate && (
                <span className="error-text">{errors.scheduledDate}</span>
              )}
            </div>

            {/* Description */}
            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea
                name="description"
                className="form-control"
                rows={4}
                placeholder="Describe the maintenance work to be performed..."
                value={formData.description}
                onChange={handleChange}
                maxLength={2000}
              />
              <span style={{ fontSize: '11px', color: '#9ca3af', textAlign: 'right' }}>
                {formData.description.length}/2000
              </span>
            </div>
          </div>

          {/* Submit buttons */}
          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/')}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={submitting}
            >
              {submitting ? '⏳ Creating...' : '✅ Create Work Order'}
            </button>
          </div>

        </form>
      </div>
    </div>
  )
}

export default CreateWorkOrder
