import React from 'react';
import { useQuery } from 'react-query';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { CustomerService } from '@/services/customerService';
import { ChargeService } from '@/services/chargeService';
import { formatCurrency, formatDate } from '@/utils/formatters';

interface CustomerDetailProps {
  customerId: number;
  onClose?: () => void;
}

export const CustomerDetail: React.FC<CustomerDetailProps> = ({
  customerId,
  onClose
}) => {
  const {
    data: customer,
    isLoading: customerLoading,
    error: customerError
  } = useQuery(
    ['customer', customerId],
    () => CustomerService.getCustomer(customerId),
    {
      enabled: !!customerId
    }
  );

  const {
    data: charges,
    isLoading: chargesLoading
  } = useQuery(
    ['customer-charges', customerId],
    () => ChargeService.getChargesByCustomer(customerId, 0, 10),
    {
      enabled: !!customerId
    }
  );

  if (customerLoading) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex items-center justify-center">
          <LoadingSpinner size="lg" />
        </div>
      </div>
    );
  }

  if (customerError || !customer) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="text-center">
          <div className="text-red-600 mb-4">
            <svg className="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">Customer not found</h3>
          <p className="text-gray-500 mb-4">The requested customer could not be found.</p>
          {onClose && (
            <button
              onClick={onClose}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
            >
              Go Back
            </button>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{customer.name}</h1>
            <p className="text-gray-600">{customer.email}</p>
            {customer.phone && (
              <p className="text-gray-600">{customer.phone}</p>
            )}
          </div>
          {onClose && (
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          )}
        </div>
      </div>

      {/* Customer Information */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Basic Information */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Basic Information</h3>
          <dl className="space-y-3">
            <div>
              <dt className="text-sm font-medium text-gray-500">Customer ID</dt>
              <dd className="text-sm text-gray-900 font-mono">#{customer.id}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Email</dt>
              <dd className="text-sm text-gray-900">{customer.email}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Phone</dt>
              <dd className="text-sm text-gray-900">{customer.phone || 'Not provided'}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Created</dt>
              <dd className="text-sm text-gray-900">{formatDate(customer.createdAt)}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Last Updated</dt>
              <dd className="text-sm text-gray-900">{formatDate(customer.updatedAt)}</dd>
            </div>
          </dl>
        </div>

        {/* Address Information */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Address</h3>
          {customer.address ? (
            <dl className="space-y-3">
              <div>
                <dt className="text-sm font-medium text-gray-500">Street</dt>
                <dd className="text-sm text-gray-900">
                  {customer.address.line1}
                  {customer.address.line2 && (
                    <div>{customer.address.line2}</div>
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">City</dt>
                <dd className="text-sm text-gray-900">{customer.address.city}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">State</dt>
                <dd className="text-sm text-gray-900">{customer.address.state || 'Not provided'}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Postal Code</dt>
                <dd className="text-sm text-gray-900">{customer.address.postalCode || 'Not provided'}</dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Country</dt>
                <dd className="text-sm text-gray-900">{customer.address.country}</dd>
              </div>
            </dl>
          ) : (
            <p className="text-gray-500">No address information available</p>
          )}
        </div>
      </div>

      {/* Recent Transactions */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Transactions</h3>
        {chargesLoading ? (
          <div className="flex items-center justify-center py-8">
            <LoadingSpinner size="lg" />
          </div>
        ) : charges && charges.content.length > 0 ? (
          <div className="space-y-3">
            {charges.content.map((charge) => (
              <div key={charge.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                <div className="flex-1">
                  <div className="flex items-center space-x-3">
                    <span className="font-mono text-sm text-gray-500">#{charge.id}</span>
                    <StatusBadge status={charge.status} />
                  </div>
                  <div className="mt-1">
                    <span className="text-sm text-gray-600">
                      {charge.description || 'No description'}
                    </span>
                  </div>
                </div>
                <div className="text-right">
                  <div className="font-semibold text-gray-900">
                    {formatCurrency(charge.amount, charge.currency)}
                  </div>
                  <div className="text-sm text-gray-500">
                    {formatDate(charge.createdAt)}
                  </div>
                </div>
              </div>
            ))}
            {charges.totalElements > charges.content.length && (
              <div className="text-center pt-4">
                <button className="text-blue-600 hover:text-blue-800 text-sm font-medium">
                  View all {charges.totalElements} transactions
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="text-center py-8">
            <div className="text-gray-400 mb-2">
              <svg className="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <p className="text-gray-500">No transactions found for this customer</p>
          </div>
        )}
      </div>
    </div>
  );
};
