import React from 'react'
import './App.css'

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>🚀 StripeFlow</h1>
        <p>Payment Integration Sandbox</p>
        <div className="features">
          <div className="feature">
            <h3>💳 Payment Processing</h3>
            <p>Secure payment processing with Stripe integration</p>
          </div>
          <div className="feature">
            <h3>👥 Customer Management</h3>
            <p>Comprehensive customer management system</p>
          </div>
          <div className="feature">
            <h3>🔔 Webhook System</h3>
            <p>Real-time event notifications and processing</p>
          </div>
          <div className="feature">
            <h3>📊 Analytics Dashboard</h3>
            <p>Real-time insights and transaction analytics</p>
          </div>
        </div>
        <div className="status">
          <h3>✅ Deployment Status</h3>
          <p>GitHub Pages deployment successful!</p>
          <p>Frontend is now live and accessible.</p>
        </div>
        <div className="links">
          <a href="https://github.com/atheendre130505/stripe-flow" target="_blank" rel="noopener noreferrer">
            View on GitHub
          </a>
          <a href="https://github.com/atheendre130505/stripe-flow/blob/main/README.md" target="_blank" rel="noopener noreferrer">
            Documentation
          </a>
        </div>
      </header>
    </div>
  )
}

export default App