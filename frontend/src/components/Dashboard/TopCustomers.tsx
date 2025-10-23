import React from 'react';
import { Users, TrendingUp } from 'lucide-react';
import { formatCurrency, formatNumber } from '../../utils';

interface TopCustomersProps {
  data?: Array<{
    customer: {
      id: string;
      name: string;
      email: string;
    };
    totalSpent: number;
    transactionCount: number;
  }>;
  loading?: boolean;
}

const TopCustomers: React.FC<TopCustomersProps> = ({ data, loading = false }) => {
  if (loading) {
    return (
      <div className="space-y-4">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="animate-pulse">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-secondary-200 rounded-full"></div>
                <div className="space-y-1">
                  <div className="h-4 bg-secondary-200 rounded w-24"></div>
                  <div className="h-3 bg-secondary-200 rounded w-32"></div>
                </div>
              </div>
              <div className="text-right space-y-1">
                <div className="h-4 bg-secondary-200 rounded w-16"></div>
                <div className="h-3 bg-secondary-200 rounded w-12"></div>
              </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="text-center text-secondary-500 py-8">
        <Users className="w-12 h-12 mx-auto mb-4 text-secondary-300" />
        <p>No customer data available</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {data.map((item, index) => (
        <div key={item.customer.id} className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
              <span className="text-sm font-medium text-primary-600">
                {item.customer.name.charAt(0).toUpperCase()}
              </span>
            </div>
            <div>
              <div className="font-medium text-secondary-900">
                {item.customer.name}
              </div>
              <div className="text-sm text-secondary-500">
                {item.customer.email}
              </div>
            </div>
          </div>
          <div className="text-right">
            <div className="font-semibold text-secondary-900">
              {formatCurrency(item.totalSpent)}
            </div>
            <div className="text-sm text-secondary-500">
              {formatNumber(item.transactionCount)} transactions
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default TopCustomers;
