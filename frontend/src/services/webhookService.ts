import { apiClient, PaginatedResponse } from './api';

// Webhook endpoint interfaces
export interface WebhookEndpoint {
  id: number;
  url: string;
  enabled: boolean;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateWebhookEndpointRequest {
  url: string;
  secret?: string;
  enabled?: boolean;
  description?: string;
}

// Webhook event interfaces
export interface WebhookEvent {
  id: number;
  endpointId: number;
  eventType: string;
  eventData: string;
  status: string;
  retryCount: number;
  maxRetries: number;
  lastAttempt?: string;
  nextRetry?: string;
  responseCode?: number;
  responseBody?: string;
  createdAt: string;
  updatedAt: string;
}

export interface WebhookStatistics {
  totalEndpoints: number;
  enabledEndpoints: number;
  totalEvents: number;
  pendingEvents: number;
  deliveredEvents: number;
  failedEvents: number;
  deliverySuccessRate: number;
}

export class WebhookService {
  // Webhook Endpoints
  static async getWebhookEndpoints(page: number = 0, size: number = 20): Promise<PaginatedResponse<WebhookEndpoint>> {
    return apiClient.getPaginated<WebhookEndpoint>('/v1/webhooks/endpoints', page, size);
  }

  static async getWebhookEndpoint(id: number): Promise<WebhookEndpoint> {
    return apiClient.get<WebhookEndpoint>(`/v1/webhooks/endpoints/${id}`);
  }

  static async createWebhookEndpoint(endpoint: CreateWebhookEndpointRequest): Promise<WebhookEndpoint> {
    return apiClient.post<WebhookEndpoint>('/v1/webhooks/endpoints', endpoint);
  }

  static async updateWebhookEndpoint(id: number, endpoint: CreateWebhookEndpointRequest): Promise<WebhookEndpoint> {
    return apiClient.put<WebhookEndpoint>(`/v1/webhooks/endpoints/${id}`, endpoint);
  }

  static async deleteWebhookEndpoint(id: number): Promise<void> {
    return apiClient.delete<void>(`/v1/webhooks/endpoints/${id}`);
  }

  static async toggleWebhookEndpoint(id: number, enabled: boolean): Promise<WebhookEndpoint> {
    return apiClient.put<WebhookEndpoint>(`/v1/webhooks/endpoints/${id}/toggle?enabled=${enabled}`);
  }

  // Webhook Events
  static async getWebhookEventsByEndpoint(endpointId: number, page: number = 0, size: number = 20): Promise<PaginatedResponse<WebhookEvent>> {
    return apiClient.getPaginated<WebhookEvent>(`/v1/webhooks/endpoints/${endpointId}/events`, page, size);
  }

  static async getWebhookEventsByStatus(status: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<WebhookEvent>> {
    return apiClient.getPaginated<WebhookEvent>(`/v1/webhooks/events/status/${status}`, page, size);
  }

  static async getRecentWebhookEvents(page: number = 0, size: number = 20): Promise<PaginatedResponse<WebhookEvent>> {
    return apiClient.getPaginated<WebhookEvent>('/v1/webhooks/events/recent', page, size);
  }

  static async getFailedWebhookEvents(page: number = 0, size: number = 20): Promise<PaginatedResponse<WebhookEvent>> {
    return apiClient.getPaginated<WebhookEvent>('/v1/webhooks/events/failed', page, size);
  }

  static async retryWebhookEvent(eventId: number): Promise<WebhookEvent> {
    return apiClient.put<WebhookEvent>(`/v1/webhooks/events/${eventId}/retry`);
  }

  // Statistics
  static async getWebhookStatistics(): Promise<WebhookStatistics> {
    return apiClient.get<WebhookStatistics>('/v1/webhooks/statistics');
  }
}
