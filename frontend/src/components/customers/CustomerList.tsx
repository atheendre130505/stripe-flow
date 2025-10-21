import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { CustomerService, Customer } from '@/services/customerService';
import { formatDate } from '@/utils/formatters';

interface CustomerListProps {
  searchTerm?: string;
  onCustomerClick?: (customer: Customer) => void;
}

export const CustomerList: React.FC<CustomerListProps> = ({
  searchTerm,
  onCustomerClick
}) => {
  const [page, setPage] = useState(0);
  const [size] = useState(20);

  const {
    data: customers,
    isLoading,
    error,
    refetch
  } = useQuery(
    ['customers', page, size, searchTerm],
    () => searchTerm 
      ? CustomerService.searchCustomers(searchTerm, page, size)
      : CustomerService.getCustomers(page, size),
    {
      keepPreviousData: true,
      staleTime: 30000,
    }
  );

  const columns = [
    {
      key: 'id' as keyof Customer,
      title: 'ID',
      render: (value: number) => (
        <span className="font-mono text-sm">#{value}</span>
      )
    },
    {
      key: 'name' as keyof Customer,
      title: 'Name',
      render: (name: string, customer: Customer) => (
        <div>
          <div className="font-medium text-gray-900">{name}</div>
          <div className="text-sm text-gray-500">{customer.email}</div>
        </div>
      )
    },
    {
      key: 'phone' as keyof Customer,
      title: 'Phone',
      render: (phone: string) => (
        <span className="text-sm text-gray-600">
          {phone || '—'}
        </span>
      )
    },
    {
      key: 'address' as keyof Customer,
      title: 'Location',
      render: (address: any) => (
        <div className="text-sm text-gray-600">
          {address ? (
            <div>
              <div>{address.city || '—'}</div>
              <div className="text-gray-500">{address.country || ''}</div>
            </div>
          ) : (
            '—'
          )}
        </div>
      )
    },
    {
      key: 'createdAt' as keyof Customer,
      title: 'Created',
      render: (date: string) => (
        <span className="text-sm text-gray-500">
          {formatDate(date)}
        </span>
      )
    }
  ];

  if (error) {
    return (
      <div className="bg-white shadow rounded-lg p-6">
        <div className="text-center">
          <div className="text-red-600 mb-4">
            <svg className="mx-auto h-12 w-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">Failed to load customers</h3>
          <p className="text-gray-500 mb-4">There was an error loading the customer data.</p>
          <button
            onClick={() => refetch()}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-lg font-medium text-gray-900">Customers</h2>
        <div className="flex items-center space-x-2">
          <button
            onClick={() => refetch()}
            disabled={isLoading}
            className="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            {isLoading ? (
              <LoadingSpinner size="sm" className="mr-2" />
            ) : (
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            )}
            Refresh
          </button>
        </div>
      </div>

      <DataTable
        data={customers?.content || []}
        columns={columns}
        loading={isLoading}
        emptyMessage="No customers found"
        onRowClick={onCustomerClick}
      />

      {/* Pagination */}
      {customers && customers.totalPages > 1 && (
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-700">
            Showing {page * size + 1} to {Math.min((page + 1) * size, customers.totalElements)} of {customers.totalElements} results
          </div>
          <div className="flex space-x-2">
            <button
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Previous
            </button>
            <button
              onClick={() => setPage(page + 1)}
              disabled={page >= customers.totalPages - 1}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
