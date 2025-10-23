import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQueryClient } from 'react-query';
import { ChargeService, CreateChargeRequest } from '@/services/chargeService';
import { CustomerService } from '@/services/customerService';
import { toast } from 'react-hot-toast';

interface CreateTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
}

interface FormData {
  amount: number;
  currency: string;
  customerId: number;
  paymentMethod?: string;
  description?: string;
  metadata?: string;
}

const CURRENCIES = [
  { value: 'USD', label: 'US Dollar (USD)' },
  { value: 'EUR', label: 'Euro (EUR)' },
  { value: 'GBP', label: 'British Pound (GBP)' },
  { value: 'INR', label: 'Indian Rupee (INR)' },
  { value: 'JPY', label: 'Japanese Yen (JPY)' }
];

export const CreateTransactionModal: React.FC<CreateTransactionModalProps> = ({
  isOpen,
  onClose
}) => {
  const [customers, setCustomers] = useState<any[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    watch
  } = useForm<FormData>();

  const createTransactionMutation = useMutation(
    (data: CreateChargeRequest) => ChargeService.createCharge(data),
    {
      onSuccess: () => {
        toast.success('Transaction created successfully');
        queryClient.invalidateQueries('transactions');
        reset();
        onClose();
      },
      onError: (error: any) => {
        toast.error(error.message || 'Failed to create transaction');
      }
    }
  );

  const searchCustomers = async (term: string) => {
    if (term.length < 2) return;
    try {
      const response = await CustomerService.getCustomers(0, 10, term);
      setCustomers(response.content);
    } catch (error) {
      console.error('Error searching customers:', error);
    }
  };

  const onSubmit = (data: FormData) => {
    createTransactionMutation.mutate({
      amount: data.amount,
      currency: data.currency,
      customerId: data.customerId,
      paymentMethod: data.paymentMethod,
      description: data.description,
      metadata: data.metadata
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">Create Transaction</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Customer Search */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Customer
              </label>
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search customers..."
                  value={searchTerm}
                  onChange={(e) => {
                    setSearchTerm(e.target.value);
                    searchCustomers(e.target.value);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {customers.length > 0 && (
                  <div className="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg max-h-40 overflow-y-auto">
                    {customers.map((customer) => (
                      <button
                        key={customer.id}
                        type="button"
                        onClick={() => {
                          setSearchTerm(customer.name);
                          setCustomers([]);
                          // Set the customer ID in the form
                        }}
                        className="w-full px-3 py-2 text-left hover:bg-gray-100"
                      >
                        <div className="font-medium">{customer.name}</div>
                        <div className="text-sm text-gray-500">{customer.email}</div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Amount */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Amount
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                {...register('amount', { required: 'Amount is required', min: 0.01 })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="0.00"
              />
              {errors.amount && (
                <p className="text-red-500 text-sm mt-1">{errors.amount.message}</p>
              )}
            </div>

            {/* Currency */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Currency
              </label>
              <select
                {...register('currency', { required: 'Currency is required' })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select currency</option>
                {CURRENCIES.map((currency) => (
                  <option key={currency.value} value={currency.value}>
                    {currency.label}
                  </option>
                ))}
              </select>
              {errors.currency && (
                <p className="text-red-500 text-sm mt-1">{errors.currency.message}</p>
              )}
            </div>

            {/* Payment Method */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Payment Method (Optional)
              </label>
              <input
                type="text"
                {...register('paymentMethod')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="card_1234567890"
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description (Optional)
              </label>
              <textarea
                {...register('description')}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Transaction description"
              />
            </div>

            {/* Actions */}
            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={createTransactionMutation.isLoading}
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
              >
                {createTransactionMutation.isLoading ? 'Creating...' : 'Create Transaction'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};


