/**
 * Navbar.jsx — Navigation bar shown at the top of every page.
 *
 * Uses React Router's <Link> component instead of <a href="...">
 * because <Link> navigates without reloading the entire page.
 * This makes the app feel fast like a native app.
 */

import { Link, useLocation } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
  // useLocation tells us the current URL so we can highlight
  // the active navigation link
  const location = useLocation()

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <span className="navbar-icon">🔧</span>
        <h1>Maintenance WO System</h1>
      </div>

      <div className="navbar-links">
        {/* Link to Screen 1: Work Order List */}
        <Link
          to="/"
          className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
        >
          📋 Work Orders
        </Link>

        {/* Link to Screen 2: Create Work Order */}
        <Link
          to="/create"
          className={`nav-link ${location.pathname === '/create' ? 'active' : ''}`}
        >
          ➕ Create Order
        </Link>
      </div>
    </nav>
  )
}

export default Navbar
