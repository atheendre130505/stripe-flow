import React from 'react';
import { useQuery } from 'react-query';
import { ChargeService } from '@/services/chargeService';
import { WebhookService } from '@/services/webhookService';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { formatCurrency, formatPercentage } from '@/utils/formatters';

interface AnalyticsDashboardProps {
  timeRange?: '24h' | '7d' | '30d' | '90d';
}

export const AnalyticsDashboard: React.FC<AnalyticsDashboardProps> = ({
  timeRange = '7d'
}) => {
  const {
    data: chargeStats,
    isLoading: chargeStatsLoading
  } = useQuery(
    ['charge-statistics', timeRange],
    () => ChargeService.getChargeStatistics(),
    {
      refetchInterval: 60000, // Refresh every minute
    }
  );

  const {
    data: webhookStats,
    isLoading: webhookStatsLoading
  } = useQuery(
    ['webhook-statistics', timeRange],
    () => WebhookService.getWebhookStatistics(),
    {
      refetchInterval: 60000,
    }
  );

  const {
    data: recentCharges,
    isLoading: recentChargesLoading
  } = useQuery(
    ['recent-charges', timeRange],
    () => ChargeService.getRecentCharges(0, 5),
    {
      refetchInterval: 30000,
    }
  );

  const isLoading = chargeStatsLoading || webhookStatsLoading || recentChargesLoading;

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="bg-white shadow rounded-lg p-6">
              <div className="animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-8 bg-gray-200 rounded w-1/2"></div>
              </div>
            </div>
          ))}
        </div>
        <div className="flex items-center justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Revenue */}
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-green-100 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Total Revenue</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {chargeStats ? formatCurrency(chargeStats.totalRevenue || 0, 'USD') : '—'}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        {/* Success Rate */}
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-blue-100 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Success Rate</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {chargeStats ? formatPercentage(chargeStats.successRate) : '—'}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        {/* Total Transactions */}
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-purple-100 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Total Transactions</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {chargeStats ? chargeStats.totalCharges.toLocaleString() : '—'}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        {/* Webhook Delivery Rate */}
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-orange-100 rounded-md flex items-center justify-center">
                <svg className="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">Webhook Delivery</dt>
                <dd className="text-lg font-medium text-gray-900">
                  {webhookStats ? formatPercentage(webhookStats.deliverySuccessRate) : '—'}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Transaction Status Chart */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Transaction Status</h3>
          {chargeStats ? (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Successful</span>
                <span className="text-sm font-medium text-gray-900">
                  {chargeStats.successfulCharges.toLocaleString()}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-green-600 h-2 rounded-full" 
                  style={{ 
                    width: `${(chargeStats.successfulCharges / chargeStats.totalCharges) * 100}%` 
                  }}
                ></div>
              </div>
              
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Failed</span>
                <span className="text-sm font-medium text-gray-900">
                  {chargeStats.failedCharges.toLocaleString()}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-red-600 h-2 rounded-full" 
                  style={{ 
                    width: `${(chargeStats.failedCharges / chargeStats.totalCharges) * 100}%` 
                  }}
                ></div>
              </div>
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">No data available</div>
          )}
        </div>

        {/* Webhook Status Chart */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Webhook Delivery</h3>
          {webhookStats ? (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Delivered</span>
                <span className="text-sm font-medium text-gray-900">
                  {webhookStats.deliveredEvents.toLocaleString()}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-green-600 h-2 rounded-full" 
                  style={{ 
                    width: `${webhookStats.deliverySuccessRate}%` 
                  }}
                ></div>
              </div>
              
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Failed</span>
                <span className="text-sm font-medium text-gray-900">
                  {webhookStats.failedEvents.toLocaleString()}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-red-600 h-2 rounded-full" 
                  style={{ 
                    width: `${(webhookStats.failedEvents / webhookStats.totalEvents) * 100}%` 
                  }}
                ></div>
              </div>
              
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Pending</span>
                <span className="text-sm font-medium text-gray-900">
                  {webhookStats.pendingEvents.toLocaleString()}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-yellow-600 h-2 rounded-full" 
                  style={{ 
                    width: `${(webhookStats.pendingEvents / webhookStats.totalEvents) * 100}%` 
                  }}
                ></div>
              </div>
            </div>
          ) : (
            <div className="text-center py-8 text-gray-500">No data available</div>
          )}
        </div>
      </div>

      {/* Recent Activity */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Transactions</h3>
        {recentCharges && recentCharges.content.length > 0 ? (
          <div className="space-y-3">
            {recentCharges.content.map((charge) => (
              <div key={charge.id} className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                <div className="flex items-center space-x-3">
                  <div className="w-2 h-2 bg-green-400 rounded-full"></div>
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      Transaction #{charge.id}
                    </div>
                    <div className="text-sm text-gray-500">
                      {charge.customer?.name || 'Unknown Customer'}
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-sm font-medium text-gray-900">
                    {formatCurrency(charge.amount, charge.currency)}
                  </div>
                  <div className="text-sm text-gray-500">
                    {new Date(charge.createdAt).toLocaleTimeString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">No recent transactions</div>
        )}
      </div>
    </div>
  );
};


