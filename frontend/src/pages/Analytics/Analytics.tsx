import React from 'react';
import { BarChart3, TrendingUp, DollarSign, CreditCard } from 'lucide-react';

const Analytics: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h1 className="text-2xl font-bold text-secondary-900">Analytics</h1>
        <p className="text-secondary-600">Detailed insights and reporting</p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-secondary-600">Total Revenue</h3>
            <DollarSign className="w-5 h-5 text-success-600" />
          </div>
          <div className="text-2xl font-bold text-secondary-900">$12,345</div>
          <div className="text-sm text-success-600">+12.5% from last month</div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-secondary-600">Transactions</h3>
            <CreditCard className="w-5 h-5 text-primary-600" />
          </div>
          <div className="text-2xl font-bold text-secondary-900">1,234</div>
          <div className="text-sm text-success-600">+8.2% from last month</div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-secondary-600">Success Rate</h3>
            <TrendingUp className="w-5 h-5 text-warning-600" />
          </div>
          <div className="text-2xl font-bold text-secondary-900">98.5%</div>
          <div className="text-sm text-success-600">+0.3% from last month</div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-medium text-secondary-600">Avg Transaction</h3>
            <BarChart3 className="w-5 h-5 text-secondary-600" />
          </div>
          <div className="text-2xl font-bold text-secondary-900">$45.67</div>
          <div className="text-sm text-success-600">+2.1% from last month</div>
        </div>
      </div>

      {/* Charts placeholder */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <h2 className="text-lg font-semibold text-secondary-900 mb-4">Revenue Trends</h2>
          <div className="h-64 flex items-center justify-center text-secondary-500">
            Chart placeholder
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <h2 className="text-lg font-semibold text-secondary-900 mb-4">Transaction Volume</h2>
          <div className="h-64 flex items-center justify-center text-secondary-500">
            Chart placeholder
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
