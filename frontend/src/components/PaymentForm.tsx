import React, { useState } from 'react'
import './PaymentForm.css'

interface PaymentFormData {
  customerName: string
  customerEmail: string
  cardNumber: string
  expiryDate: string
  cvv: string
  amount: number
  currency: string
  description: string
}

interface PaymentFormProps {
  onPaymentSuccess: (transactionId: string, amount: number) => void
  onPaymentError: (error: string) => void
}

const PaymentForm: React.FC<PaymentFormProps> = ({ onPaymentSuccess, onPaymentError }) => {
  const [formData, setFormData] = useState<PaymentFormData>({
    customerName: '',
    customerEmail: '',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    amount: 0,
    currency: 'USD',
    description: ''
  })
  
  const [isProcessing, setIsProcessing] = useState(false)

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const formatCardNumber = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '')
    const matches = v.match(/\d{4,16}/g)
    const match = matches && matches[0] || ''
    const parts = []
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4))
    }
    if (parts.length) {
      return parts.join(' ')
    } else {
      return v
    }
  }

  const handleCardNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatCardNumber(e.target.value)
    setFormData(prev => ({
      ...prev,
      cardNumber: formatted
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsProcessing(true)

    try {
      // Simulate payment processing
      const response = await fetch('/api/charges', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          amount: formData.amount * 100, // Convert to cents
          currency: formData.currency,
          description: formData.description,
          customer: {
            name: formData.customerName,
            email: formData.customerEmail
          },
          paymentMethod: {
            type: 'card',
            card: {
              number: formData.cardNumber.replace(/\s/g, ''),
              expiryMonth: formData.expiryDate.split('/')[0],
              expiryYear: formData.expiryDate.split('/')[1],
              cvc: formData.cvv
            }
          }
        })
      })

      const result = await response.json()

      if (response.ok) {
        onPaymentSuccess(result.id, formData.amount)
        // Reset form
        setFormData({
          customerName: '',
          customerEmail: '',
          cardNumber: '',
          expiryDate: '',
          cvv: '',
          amount: 0,
          currency: 'USD',
          description: ''
        })
      } else {
        onPaymentError(result.message || 'Payment failed')
      }
    } catch (error) {
      onPaymentError('Network error. Please try again.')
    } finally {
      setIsProcessing(false)
    }
  }

  return (
    <div className="payment-form-container">
      <h2>💳 Process Payment</h2>
      <form onSubmit={handleSubmit} className="payment-form">
        <div className="form-section">
          <h3>Customer Information</h3>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="customerName">Full Name</label>
              <input
                type="text"
                id="customerName"
                name="customerName"
                value={formData.customerName}
                onChange={handleInputChange}
                required
                placeholder="John Doe"
              />
            </div>
            <div className="form-group">
              <label htmlFor="customerEmail">Email</label>
              <input
                type="email"
                id="customerEmail"
                name="customerEmail"
                value={formData.customerEmail}
                onChange={handleInputChange}
                required
                placeholder="john@example.com"
              />
            </div>
          </div>
        </div>

        <div className="form-section">
          <h3>Payment Details</h3>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="amount">Amount</label>
              <input
                type="number"
                id="amount"
                name="amount"
                value={formData.amount}
                onChange={handleInputChange}
                required
                min="0.01"
                step="0.01"
                placeholder="0.00"
              />
            </div>
            <div className="form-group">
              <label htmlFor="currency">Currency</label>
              <select
                id="currency"
                name="currency"
                value={formData.currency}
                onChange={handleInputChange}
                required
              >
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="GBP">GBP</option>
                <option value="CAD">CAD</option>
                <option value="AUD">AUD</option>
              </select>
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Payment for services"
              rows={3}
            />
          </div>
        </div>

        <div className="form-section">
          <h3>Card Information</h3>
          <div className="form-group">
            <label htmlFor="cardNumber">Card Number</label>
            <input
              type="text"
              id="cardNumber"
              name="cardNumber"
              value={formData.cardNumber}
              onChange={handleCardNumberChange}
              required
              placeholder="1234 5678 9012 3456"
              maxLength={19}
            />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="expiryDate">Expiry Date</label>
              <input
                type="text"
                id="expiryDate"
                name="expiryDate"
                value={formData.expiryDate}
                onChange={(e) => {
                  const value = e.target.value.replace(/\D/g, '')
                  if (value.length <= 4) {
                    const formatted = value.replace(/(\d{2})(\d{0,2})/, '$1/$2')
                    setFormData(prev => ({ ...prev, expiryDate: formatted }))
                  }
                }}
                required
                placeholder="MM/YY"
                maxLength={5}
              />
            </div>
            <div className="form-group">
              <label htmlFor="cvv">CVV</label>
              <input
                type="text"
                id="cvv"
                name="cvv"
                value={formData.cvv}
                onChange={handleInputChange}
                required
                placeholder="123"
                maxLength={4}
              />
            </div>
          </div>
        </div>

        <button 
          type="submit" 
          className="submit-button"
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'Process Payment'}
        </button>
      </form>
    </div>
  )
}

export default PaymentForm
