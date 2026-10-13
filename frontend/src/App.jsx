/**
 * App.jsx — Root component that sets up routing between screens.
 *
 * React Router's <Routes> and <Route> components work like a switch statement:
 * - If the URL is "/",         show WorkOrderList
 * - If the URL is "/create",   show CreateWorkOrder
 * - If the URL is "/orders/:id", show WorkOrderDetail
 *
 * The <Navbar> component is shown on every page.
 */

import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import WorkOrderList from './pages/WorkOrderList'
import CreateWorkOrder from './pages/CreateWorkOrder'
import WorkOrderDetail from './pages/WorkOrderDetail'
import './App.css'

function App() {
  return (
    <div className="app-container">
      {/* Navigation bar — always visible at the top */}
      <Navbar />

      {/* Main content area — changes based on the URL */}
      <main className="main-content">
        <Routes>
          {/* Screen 1: Work Order List (home page) */}
          <Route path="/" element={<WorkOrderList />} />

          {/* Screen 2: Create Work Order form */}
          <Route path="/create" element={<CreateWorkOrder />} />

          {/* Screen 3: Work Order Detail — :id is a URL parameter */}
          <Route path="/orders/:id" element={<WorkOrderDetail />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
