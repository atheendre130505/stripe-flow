import React from 'react'
import './PaymentReceipt.css'

interface PaymentReceiptProps {
  transactionId: string
  amount: number
  currency: string
  customerName: string
  customerEmail: string
  description: string
  status: 'succeeded' | 'failed'
  timestamp: string
  onClose: () => void
  onNewPayment: () => void
}

const PaymentReceipt: React.FC<PaymentReceiptProps> = ({
  transactionId,
  amount,
  currency,
  customerName,
  customerEmail,
  description,
  status,
  timestamp,
  onClose,
  onNewPayment
}) => {
  const formatCurrency = (amount: number, currency: string) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(amount / 100)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  }

  const isSuccess = status === 'succeeded'

  return (
    <div className="payment-receipt-overlay">
      <div className="payment-receipt">
        <div className="receipt-header">
          <div className={`status-icon ${isSuccess ? 'success' : 'error'}`}>
            {isSuccess ? '✅' : '❌'}
          </div>
          <h2 className={isSuccess ? 'success-text' : 'error-text'}>
            {isSuccess ? 'Payment Successful!' : 'Payment Failed'}
          </h2>
        </div>

        <div className="receipt-content">
          <div className="receipt-section">
            <h3>Transaction Details</h3>
            <div className="receipt-row">
              <span className="label">Transaction ID:</span>
              <span className="value transaction-id">{transactionId}</span>
            </div>
            <div className="receipt-row">
              <span className="label">Amount:</span>
              <span className="value amount">{formatCurrency(amount, currency)}</span>
            </div>
            <div className="receipt-row">
              <span className="label">Status:</span>
              <span className={`value status ${status}`}>
                {status.charAt(0).toUpperCase() + status.slice(1)}
              </span>
            </div>
            <div className="receipt-row">
              <span className="label">Date:</span>
              <span className="value">{formatDate(timestamp)}</span>
            </div>
            {description && (
              <div className="receipt-row">
                <span className="label">Description:</span>
                <span className="value">{description}</span>
              </div>
            )}
          </div>

          <div className="receipt-section">
            <h3>Customer Information</h3>
            <div className="receipt-row">
              <span className="label">Name:</span>
              <span className="value">{customerName}</span>
            </div>
            <div className="receipt-row">
              <span className="label">Email:</span>
              <span className="value">{customerEmail}</span>
            </div>
          </div>

          {!isSuccess && (
            <div className="error-message">
              <p>Your payment could not be processed. Please check your payment information and try again.</p>
            </div>
          )}
        </div>

        <div className="receipt-actions">
          <button onClick={onNewPayment} className="new-payment-button">
            Process New Payment
          </button>
          <button onClick={onClose} className="close-button">
            Close
          </button>
        </div>

        <div className="receipt-footer">
          <p>Thank you for using StripeFlow Payment System</p>
          <p className="support-text">
            Need help? Contact support at support@stripeflow.com
          </p>
        </div>
      </div>
    </div>
  )
}

export default PaymentReceipt
