import React from 'react';
import { useQuery } from 'react-query';
import { 
  CreditCard, 
  Users, 
  DollarSign, 
  TrendingUp,
  Activity,
  ArrowUpRight,
  ArrowDownRight
} from 'lucide-react';
import { getDashboardStats, getRevenueChartData, getTopCustomers } from '../../services/api';
import { formatCurrency, formatNumber } from '../../utils';
import StatsCard from '../../components/Dashboard/StatsCard';
import RevenueChart from '../../components/Dashboard/RevenueChart';
import RecentTransactions from '../../components/Dashboard/RecentTransactions';
import TopCustomers from '../../components/Dashboard/TopCustomers';

const Dashboard: React.FC = () => {
  // Fetch dashboard data
  const { data: stats, isLoading: statsLoading } = useQuery(
    'dashboard-stats',
    getDashboardStats,
    { refetchInterval: 30000 }
  );

  const { data: revenueData, isLoading: revenueLoading } = useQuery(
    'revenue-chart',
    () => getRevenueChartData('30d'),
    { refetchInterval: 60000 }
  );

  const { data: topCustomers, isLoading: customersLoading } = useQuery(
    'top-customers',
    () => getTopCustomers(5),
    { refetchInterval: 300000 }
  );

  const statsCards = [
    {
      title: 'Total Revenue',
      value: stats?.totalRevenue ? formatCurrency(stats.totalRevenue) : '$0',
      change: stats?.monthlyRevenue ? `+${formatNumber(stats.monthlyRevenue / stats.totalRevenue * 100, 1)}%` : '+0%',
      changeType: 'positive' as const,
      icon: DollarSign,
      color: 'success',
    },
    {
      title: 'Total Transactions',
      value: stats?.totalTransactions ? formatNumber(stats.totalTransactions) : '0',
      change: stats?.dailyTransactions ? `+${stats.dailyTransactions} today` : '+0 today',
      changeType: 'positive' as const,
      icon: CreditCard,
      color: 'primary',
    },
    {
      title: 'Success Rate',
      value: stats?.successRate ? `${formatNumber(stats.successRate, 1)}%` : '0%',
      change: stats?.successRate && stats.successRate > 95 ? '+2.1%' : '-1.2%',
      changeType: stats?.successRate && stats.successRate > 95 ? 'positive' as const : 'negative' as const,
      icon: TrendingUp,
      color: stats?.successRate && stats.successRate > 95 ? 'success' : 'warning',
    },
    {
      title: 'Avg Transaction',
      value: stats?.averageTransactionValue ? formatCurrency(stats.averageTransactionValue) : '$0',
      change: '+5.2%',
      changeType: 'positive' as const,
      icon: Activity,
      color: 'secondary',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h1 className="text-2xl font-bold text-secondary-900">Dashboard</h1>
        <p className="text-secondary-600">Welcome back! Here's what's happening with your payments.</p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statsCards.map((card, index) => (
          <StatsCard
            key={index}
            title={card.title}
            value={card.value}
            change={card.change}
            changeType={card.changeType}
            icon={card.icon}
            color={card.color as 'primary' | 'secondary' | 'success' | 'warning' | 'error'}
            loading={statsLoading}
          />
        ))}
      </div>

      {/* Charts and tables */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue chart */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-secondary-900">Revenue Overview</h2>
            <div className="flex space-x-2">
              <button className="px-3 py-1 text-xs font-medium text-primary-600 bg-primary-50 rounded-full">
                30 Days
              </button>
              <button className="px-3 py-1 text-xs font-medium text-secondary-600 hover:text-secondary-900">
                90 Days
              </button>
            </div>
          </div>
          <RevenueChart data={revenueData} loading={revenueLoading} />
        </div>

        {/* Top customers */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-secondary-900">Top Customers</h2>
            <button className="text-sm text-primary-600 hover:text-primary-700">
              View all
            </button>
          </div>
          <TopCustomers data={topCustomers} loading={customersLoading} />
        </div>
      </div>

      {/* Recent transactions */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200">
        <div className="p-6 border-b border-secondary-200">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-secondary-900">Recent Transactions</h2>
            <button className="text-sm text-primary-600 hover:text-primary-700">
              View all
            </button>
          </div>
        </div>
        <RecentTransactions />
      </div>
    </div>
  );
};

export default Dashboard;
