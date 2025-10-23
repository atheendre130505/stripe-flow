import React, { useState } from 'react';
import { X, CreditCard, User } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { chargeService } from '../../services/chargeService';
import { CreateChargeRequest } from '../../types';
import { Button } from '../UI/Button';
import { Input } from '../UI/Input';

interface CreateTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

interface FormData {
  amount: string;
  currency: string;
  description: string;
  customerName: string;
  customerEmail: string;
  cardNumber: string;
  expiryMonth: string;
  expiryYear: string;
  cvc: string;
}

const CreateTransactionModal: React.FC<CreateTransactionModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>();

  const onSubmit = async (data: FormData) => {
    setIsSubmitting(true);
    try {
      const chargeData: CreateChargeRequest = {
        amount: Math.round(parseFloat(data.amount) * 100), // Convert to cents
        currency: data.currency,
        description: data.description,
        customer: {
          name: data.customerName,
          email: data.customerEmail,
        },
        paymentMethod: {
          type: 'card',
          card: {
            number: data.cardNumber.replace(/\s/g, ''),
            expMonth: parseInt(data.expiryMonth),
            expYear: parseInt(data.expiryYear),
            cvc: data.cvc,
          },
        },
      };

      await chargeService.createCharge(chargeData);
      toast.success('Transaction created successfully!');
      reset();
      onSuccess();
    } catch (error) {
      toast.error('Failed to create transaction');
      console.error('Transaction creation error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div className="fixed inset-0 transition-opacity" onClick={onClose}>
          <div className="absolute inset-0 bg-secondary-500 opacity-75"></div>
        </div>

        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-medium text-secondary-900">
                  Create New Transaction
                </h3>
                <button
                  type="button"
                  onClick={onClose}
                  className="text-secondary-400 hover:text-secondary-600"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <div className="space-y-4">
                {/* Customer Information */}
                <div className="border-b border-secondary-200 pb-4">
                  <div className="flex items-center mb-3">
                    <User className="w-5 h-5 text-primary-600 mr-2" />
                    <h4 className="text-sm font-medium text-secondary-900">Customer Information</h4>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input
                      label="Customer Name"
                      {...register('customerName', { required: 'Customer name is required' })}
                      error={errors.customerName?.message}
                    />
                    <Input
                      label="Customer Email"
                      type="email"
                      {...register('customerEmail', { 
                        required: 'Customer email is required',
                        pattern: {
                          value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                          message: 'Invalid email address'
                        }
                      })}
                      error={errors.customerEmail?.message}
                    />
                  </div>
                </div>

                {/* Transaction Details */}
                <div className="border-b border-secondary-200 pb-4">
                  <div className="flex items-center mb-3">
                    <CreditCard className="w-5 h-5 text-primary-600 mr-2" />
                    <h4 className="text-sm font-medium text-secondary-900">Transaction Details</h4>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input
                      label="Amount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      {...register('amount', { 
                        required: 'Amount is required',
                        min: { value: 0.01, message: 'Amount must be greater than 0' }
                      })}
                      error={errors.amount?.message}
                    />
                    <div>
                      <label className="block text-sm font-medium text-secondary-700 mb-1">
                        Currency
                      </label>
                      <select
                        {...register('currency', { required: 'Currency is required' })}
                        className="block w-full px-3 py-2 border border-secondary-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      >
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                        <option value="GBP">GBP</option>
                        <option value="CAD">CAD</option>
                      </select>
                      {errors.currency && (
                        <p className="text-sm text-error-600 mt-1">{errors.currency.message}</p>
                      )}
                    </div>
                  </div>
                  <div className="mt-4">
                    <Input
                      label="Description"
                      {...register('description')}
                      placeholder="Optional description"
                    />
                  </div>
                </div>

                {/* Payment Method */}
                <div>
                  <div className="flex items-center mb-3">
                    <CreditCard className="w-5 h-5 text-primary-600 mr-2" />
                    <h4 className="text-sm font-medium text-secondary-900">Payment Method</h4>
                  </div>
                  <div className="space-y-4">
                    <Input
                      label="Card Number"
                      placeholder="1234 5678 9012 3456"
                      {...register('cardNumber', { 
                        required: 'Card number is required',
                        pattern: {
                          value: /^[0-9\s]{13,19}$/,
                          message: 'Invalid card number'
                        }
                      })}
                      error={errors.cardNumber?.message}
                    />
                    <div className="grid grid-cols-3 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-secondary-700 mb-1">
                          Expiry Month
                        </label>
                        <select
                          {...register('expiryMonth', { required: 'Expiry month is required' })}
                          className="block w-full px-3 py-2 border border-secondary-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        >
                          {Array.from({ length: 12 }, (_, i) => (
                            <option key={i + 1} value={i + 1}>
                              {String(i + 1).padStart(2, '0')}
                            </option>
                          ))}
                        </select>
                        {errors.expiryMonth && (
                          <p className="text-sm text-error-600 mt-1">{errors.expiryMonth.message}</p>
                        )}
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-secondary-700 mb-1">
                          Expiry Year
                        </label>
                        <select
                          {...register('expiryYear', { required: 'Expiry year is required' })}
                          className="block w-full px-3 py-2 border border-secondary-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                        >
                          {Array.from({ length: 10 }, (_, i) => {
                            const year = new Date().getFullYear() + i;
                            return (
                              <option key={year} value={year}>
                                {year}
                              </option>
                            );
                          })}
                        </select>
                        {errors.expiryYear && (
                          <p className="text-sm text-error-600 mt-1">{errors.expiryYear.message}</p>
                        )}
                      </div>
                      <Input
                        label="CVC"
                        placeholder="123"
                        {...register('cvc', { 
                          required: 'CVC is required',
                          pattern: {
                            value: /^[0-9]{3,4}$/,
                            message: 'Invalid CVC'
                          }
                        })}
                        error={errors.cvc?.message}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-secondary-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
              <Button
                type="submit"
                loading={isSubmitting}
                className="w-full sm:w-auto sm:ml-3"
              >
                Create Transaction
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                className="mt-3 w-full sm:mt-0 sm:w-auto"
              >
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateTransactionModal;
