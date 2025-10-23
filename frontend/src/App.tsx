import React, { useState } from 'react'
import PaymentForm from './components/PaymentForm'
import TransactionHistory from './components/TransactionHistory'
import PaymentReceipt from './components/PaymentReceipt'
import './App.css'

interface Transaction {
  id: string
  amount: number
  currency: string
  status: 'succeeded' | 'failed' | 'pending'
  description: string
  customerName: string
  customerEmail: string
  createdAt: string
  updatedAt: string
}

function App() {
  const [currentView, setCurrentView] = useState<'home' | 'payment' | 'history'>('home')
  const [showReceipt, setShowReceipt] = useState(false)
  const [receiptData, setReceiptData] = useState<{
    transactionId: string
    amount: number
    currency: string
    customerName: string
    customerEmail: string
    description: string
    status: 'succeeded' | 'failed'
    timestamp: string
  } | null>(null)

  const handlePaymentSuccess = (transactionId: string, amount: number) => {
    setReceiptData({
      transactionId,
      amount,
      currency: 'USD',
      customerName: 'Customer',
      customerEmail: 'customer@example.com',
      description: 'Payment processed',
      status: 'succeeded',
      timestamp: new Date().toISOString()
    })
    setShowReceipt(true)
  }

  const handlePaymentError = (error: string) => {
    setReceiptData({
      transactionId: 'TXN_' + Math.random().toString(36).substr(2, 9),
      amount: 0,
      currency: 'USD',
      customerName: 'Customer',
      customerEmail: 'customer@example.com',
      description: 'Payment failed',
      status: 'failed',
      timestamp: new Date().toISOString()
    })
    setShowReceipt(true)
  }

  const handleTransactionClick = (transaction: Transaction) => {
    setReceiptData({
      transactionId: transaction.id,
      amount: transaction.amount,
      currency: transaction.currency,
      customerName: transaction.customerName,
      customerEmail: transaction.customerEmail,
      description: transaction.description,
      status: transaction.status as 'succeeded' | 'failed',
      timestamp: transaction.createdAt
    })
    setShowReceipt(true)
  }

  const handleCloseReceipt = () => {
    setShowReceipt(false)
    setReceiptData(null)
  }

  const handleNewPayment = () => {
    setShowReceipt(false)
    setReceiptData(null)
    setCurrentView('payment')
  }

  if (showReceipt && receiptData) {
    return (
      <PaymentReceipt
        {...receiptData}
        onClose={handleCloseReceipt}
        onNewPayment={handleNewPayment}
      />
    )
  }

  return (
    <div className="App">
      <nav className="app-nav">
        <div className="nav-brand">
          <h1>🚀 StripeFlow</h1>
        </div>
        <div className="nav-links">
          <button 
            className={currentView === 'home' ? 'active' : ''} 
            onClick={() => setCurrentView('home')}
          >
            Home
          </button>
          <button 
            className={currentView === 'payment' ? 'active' : ''} 
            onClick={() => setCurrentView('payment')}
          >
            Process Payment
          </button>
          <button 
            className={currentView === 'history' ? 'active' : ''} 
            onClick={() => setCurrentView('history')}
          >
            Transaction History
          </button>
        </div>
      </nav>

      <main className="app-main">
        {currentView === 'home' && (
          <div className="home-content">
            <div className="hero-section">
              <h2>Payment Integration Sandbox</h2>
              <p>A production-ready payment processing platform with comprehensive features for modern e-commerce.</p>
            </div>
            
            <div className="features">
              <div className="feature">
                <h3>💳 Payment Processing</h3>
                <p>Secure payment processing with Stripe integration</p>
                <button onClick={() => setCurrentView('payment')} className="feature-button">
                  Process Payment
                </button>
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
                <button onClick={() => setCurrentView('history')} className="feature-button">
                  View Transactions
                </button>
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
          </div>
        )}

        {currentView === 'payment' && (
          <PaymentForm
            onPaymentSuccess={handlePaymentSuccess}
            onPaymentError={handlePaymentError}
          />
        )}

        {currentView === 'history' && (
          <TransactionHistory
            onTransactionClick={handleTransactionClick}
          />
        )}
      </main>
    </div>
  )
}

export default App