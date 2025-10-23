import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { DataTable } from '@/components/ui/DataTable';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { WebhookService, WebhookEvent } from '@/services/webhookService';
import { formatDate, formatRelativeTime } from '@/utils/formatters';
import { toast } from 'react-hot-toast';

interface WebhookLogsProps {
  endpointId?: number;
  status?: string;
}

export const WebhookLogs: React.FC<WebhookLogsProps> = ({
  endpointId,
  status
}) => {
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const queryClient = useQueryClient();

  const {
    data: events,
    isLoading,
    error,
    refetch
  } = useQuery(
    ['webhook-events', endpointId, status, page, size],
    () => {
      if (endpointId) {
        return WebhookService.getWebhookEventsByEndpoint(endpointId, page, size);
      } else if (status) {
        return WebhookService.getWebhookEventsByStatus(status, page, size);
      } else {
        return WebhookService.getRecentWebhookEvents(page, size);
      }
    },
    {
      keepPreviousData: true,
      staleTime: 10000, // 10 seconds for real-time updates
      refetchInterval: 30000, // Auto-refresh every 30 seconds
    }
  );

  const retryMutation = useMutation(
    (eventId: number) => WebhookService.retryWebhookEvent(eventId),
    {
      onSuccess: () => {
        toast.success('Webhook event retry initiated');
        queryClient.invalidateQueries('webhook-events');
      },
      onError: (error: any) => {
        toast.error(error.message || 'Failed to retry webhook event');
      }
    }
  );

  const handleRetry = (eventId: number) => {
    retryMutation.mutate(eventId);
  };

  const columns = [
    {
      key: 'id' as keyof WebhookEvent,
      title: 'ID',
      render: (value: number) => (
        <span className="font-mono text-sm">#{value}</span>
      )
    },
    {
      key: 'eventType' as keyof WebhookEvent,
      title: 'Event Type',
      render: (eventType: string) => (
        <span className="font-medium text-gray-900">{eventType}</span>
      )
    },
    {
      key: 'status' as keyof WebhookEvent,
      title: 'Status',
      render: (status: string) => <StatusBadge status={status} />
    },
    {
      key: 'retryCount' as keyof WebhookEvent,
      title: 'Retries',
      render: (retryCount: number, event: WebhookEvent) => (
        <div className="text-sm">
          <span className="text-gray-900">{retryCount}</span>
          <span className="text-gray-500">/{event.maxRetries}</span>
        </div>
      )
    },
    {
      key: 'responseCode' as keyof WebhookEvent,
      title: 'Response',
      render: (responseCode: number) => (
        <div className="text-sm">
          {responseCode ? (
            <span className={`font-mono ${
              responseCode >= 200 && responseCode < 300 
                ? 'text-green-600' 
                : responseCode >= 400 
                ? 'text-red-600' 
                : 'text-yellow-600'
            }`}>
              {responseCode}
            </span>
          ) : (
            <span className="text-gray-400">—</span>
          )}
        </div>
      )
    },
    {
      key: 'lastAttempt' as keyof WebhookEvent,
      title: 'Last Attempt',
      render: (lastAttempt: string) => (
        <div className="text-sm text-gray-500">
          {lastAttempt ? formatRelativeTime(lastAttempt) : 'Never'}
        </div>
      )
    },
    {
      key: 'createdAt' as keyof WebhookEvent,
      title: 'Created',
      render: (createdAt: string) => (
        <div className="text-sm text-gray-500">
          {formatDate(createdAt)}
        </div>
      )
    },
    {
      key: 'actions' as keyof WebhookEvent,
      title: 'Actions',
      render: (value: any, event: WebhookEvent) => (
        <div className="flex space-x-2">
          {event.status === 'FAILED' && (
            <button
              onClick={() => handleRetry(event.id)}
              disabled={retryMutation.isLoading}
              className="text-blue-600 hover:text-blue-800 text-sm font-medium disabled:opacity-50"
            >
              Retry
            </button>
          )}
          <button
            onClick={() => {
              // Show event details modal
              console.log('Show event details:', event);
            }}
            className="text-gray-600 hover:text-gray-800 text-sm font-medium"
          >
            View
          </button>
        </div>
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
          <h3 className="text-lg font-medium text-gray-900 mb-2">Failed to load webhook logs</h3>
          <p className="text-gray-500 mb-4">There was an error loading the webhook event data.</p>
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
        <h2 className="text-lg font-medium text-gray-900">Webhook Logs</h2>
        <div className="flex items-center space-x-2">
          <div className="text-sm text-gray-500">
            Auto-refreshing every 30s
          </div>
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
        data={events?.content || []}
        columns={columns}
        loading={isLoading}
        emptyMessage="No webhook events found"
      />

      {/* Pagination */}
      {events && events.totalPages > 1 && (
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-700">
            Showing {page * size + 1} to {Math.min((page + 1) * size, events.totalElements)} of {events.totalElements} results
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
              disabled={page >= events.totalPages - 1}
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


