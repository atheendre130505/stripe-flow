import React, { useState, useEffect } from 'react'
import './TransactionHistory.css'

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

interface TransactionHistoryProps {
  onTransactionClick: (transaction: Transaction) => void
}

const TransactionHistory: React.FC<TransactionHistoryProps> = ({ onTransactionClick }) => {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filters, setFilters] = useState({
    status: '',
    search: ''
  })

  useEffect(() => {
    fetchTransactions()
  }, [])

  const fetchTransactions = async () => {
    try {
      setLoading(true)
      const response = await fetch('/api/charges')
      if (response.ok) {
        const data = await response.json()
        setTransactions(data)
      } else {
        setError('Failed to fetch transactions')
      }
    } catch (err) {
      setError('Network error')
    } finally {
      setLoading(false)
    }
  }

  const filteredTransactions = transactions.filter(transaction => {
    const matchesStatus = !filters.status || transaction.status === filters.status
    const matchesSearch = !filters.search || 
      transaction.customerName.toLowerCase().includes(filters.search.toLowerCase()) ||
      transaction.customerEmail.toLowerCase().includes(filters.search.toLowerCase()) ||
      transaction.description.toLowerCase().includes(filters.search.toLowerCase())
    
    return matchesStatus && matchesSearch
  })

  const formatCurrency = (amount: number, currency: string) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(amount / 100)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const getStatusBadge = (status: string) => {
    const statusClasses = {
      succeeded: 'status-succeeded',
      failed: 'status-failed',
      pending: 'status-pending'
    }
    
    return (
      <span className={`status-badge ${statusClasses[status as keyof typeof statusClasses] || 'status-unknown'}`}>
        {status.charAt(0).toUpperCase() + status.slice(1)}
      </span>
    )
  }

  if (loading) {
    return (
      <div className="transaction-history">
        <div className="loading">Loading transactions...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="transaction-history">
        <div className="error">Error: {error}</div>
        <button onClick={fetchTransactions} className="retry-button">
          Retry
        </button>
      </div>
    )
  }

  return (
    <div className="transaction-history">
      <div className="history-header">
        <h2>📊 Transaction History</h2>
        <div className="filters">
          <input
            type="text"
            placeholder="Search transactions..."
            value={filters.search}
            onChange={(e) => setFilters(prev => ({ ...prev, search: e.target.value }))}
            className="search-input"
          />
          <select
            value={filters.status}
            onChange={(e) => setFilters(prev => ({ ...prev, status: e.target.value }))}
            className="status-filter"
          >
            <option value="">All Status</option>
            <option value="succeeded">Succeeded</option>
            <option value="failed">Failed</option>
            <option value="pending">Pending</option>
          </select>
        </div>
      </div>

      <div className="transactions-list">
        {filteredTransactions.length === 0 ? (
          <div className="no-transactions">
            <p>No transactions found</p>
          </div>
        ) : (
          filteredTransactions.map((transaction) => (
            <div
              key={transaction.id}
              className="transaction-item"
              onClick={() => onTransactionClick(transaction)}
            >
              <div className="transaction-main">
                <div className="transaction-info">
                  <h3>{transaction.customerName}</h3>
                  <p className="customer-email">{transaction.customerEmail}</p>
                  <p className="transaction-description">{transaction.description}</p>
                </div>
                <div className="transaction-amount">
                  <span className="amount">{formatCurrency(transaction.amount, transaction.currency)}</span>
                  {getStatusBadge(transaction.status)}
                </div>
              </div>
              <div className="transaction-meta">
                <span className="transaction-id">ID: {transaction.id}</span>
                <span className="transaction-date">{formatDate(transaction.createdAt)}</span>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="history-footer">
        <p>Total: {filteredTransactions.length} transactions</p>
        <button onClick={fetchTransactions} className="refresh-button">
          Refresh
        </button>
      </div>
    </div>
  )
}

export default TransactionHistory
