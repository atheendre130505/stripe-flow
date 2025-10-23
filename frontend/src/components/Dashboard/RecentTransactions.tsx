import React from 'react';
import { useQuery } from 'react-query';
import { CreditCard, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { chargeService } from '../../services/chargeService';
import { formatCurrency, formatDateTime, getStatusColor, getStatusText } from '../../utils';

const RecentTransactions: React.FC = () => {
  const { data, isLoading } = useQuery(
    'recent-transactions',
    () => chargeService.getCharges(1, 10),
    { refetchInterval: 30000 }
  );

  if (isLoading) {
    return (
      <div className="p-6">
        <div className="space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-secondary-200 rounded-lg"></div>
                  <div className="space-y-2">
                    <div className="h-4 bg-secondary-200 rounded w-32"></div>
                    <div className="h-3 bg-secondary-200 rounded w-24"></div>
                  </div>
                </div>
                <div className="text-right space-y-2">
                  <div className="h-4 bg-secondary-200 rounded w-20"></div>
                  <div className="h-3 bg-secondary-200 rounded w-16"></div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (!data?.data || data.data.length === 0) {
    return (
      <div className="p-6 text-center text-secondary-500">
        <CreditCard className="w-12 h-12 mx-auto mb-4 text-secondary-300" />
        <p>No recent transactions</p>
      </div>
    );
  }

  return (
    <div className="divide-y divide-secondary-200">
      {data.data.map((transaction) => (
        <div key={transaction.id} className="p-6 hover:bg-secondary-50 transition-colors">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center">
                <CreditCard className="w-5 h-5 text-primary-600" />
              </div>
              <div>
                <div className="font-medium text-secondary-900">
                  {transaction.customer?.name || 'Unknown Customer'}
                </div>
                <div className="text-sm text-secondary-500">
                  {transaction.description || 'Payment'}
                </div>
              </div>
            </div>
            <div className="text-right">
              <div className="font-semibold text-secondary-900">
                {formatCurrency(transaction.amount, transaction.currency)}
              </div>
              <div className="flex items-center space-x-2">
                <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(transaction.status)}`}>
                  {getStatusText(transaction.status)}
                </span>
                <span className="text-sm text-secondary-500">
                  {formatDateTime(transaction.createdAt)}
                </span>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default RecentTransactions;
