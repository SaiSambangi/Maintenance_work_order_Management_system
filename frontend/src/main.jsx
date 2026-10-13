/**
 * main.jsx — Entry point for our React application.
 *
 * This is the very first file React looks at.
 * It finds the <div id="root"> in index.html and renders our <App> inside it.
 *
 * BrowserRouter enables React Router — it watches the browser URL and shows
 * the right page component when the URL changes.
 */

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* BrowserRouter wraps everything so any component can use routing */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>,
)
