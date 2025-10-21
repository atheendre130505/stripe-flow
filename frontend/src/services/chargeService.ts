import { apiClient, PaginatedResponse } from './api';
import { Charge, CreateChargeRequest } from '@/types';

export class ChargeService {
  // Get all charges with pagination
  static async getCharges(page: number = 0, size: number = 20, filters?: ChargeFilters): Promise<PaginatedResponse<Charge>> {
    const params: Record<string, any> = { page, size };
    if (filters) {
      Object.assign(params, filters);
    }
    return apiClient.getPaginated<Charge>('/v1/charges', page, size, params);
  }

  // Get charge by ID
  static async getCharge(id: number): Promise<Charge> {
    return apiClient.get<Charge>(`/v1/charges/${id}`);
  }

  // Create new charge
  static async createCharge(charge: CreateChargeRequest): Promise<Charge> {
    return apiClient.post<Charge>('/v1/charges', charge);
  }

  // Get charges by customer
  static async getChargesByCustomer(customerId: number, page: number = 0, size: number = 20): Promise<PaginatedResponse<Charge>> {
    return apiClient.getPaginated<Charge>(`/v1/charges/customer/${customerId}`, page, size);
  }

  // Get charges by status
  static async getChargesByStatus(status: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<Charge>> {
    return apiClient.getPaginated<Charge>(`/v1/charges/status/${status}`, page, size);
  }

  // Get charges by currency
  static async getChargesByCurrency(currency: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<Charge>> {
    return apiClient.getPaginated<Charge>(`/v1/charges/currency/${currency}`, page, size);
  }

  // Get recent charges
  static async getRecentCharges(page: number = 0, size: number = 20): Promise<PaginatedResponse<Charge>> {
    return apiClient.getPaginated<Charge>('/v1/charges/recent', page, size);
  }

  // Update charge status
  static async updateChargeStatus(id: number, status: string): Promise<Charge> {
    return apiClient.put<Charge>(`/v1/charges/${id}/status?status=${status}`);
  }

  // Cancel charge
  static async cancelCharge(id: number): Promise<Charge> {
    return apiClient.put<Charge>(`/v1/charges/${id}/cancel`);
  }

  // Get charge statistics
  static async getChargeStatistics(): Promise<ChargeStatistics> {
    return apiClient.get<ChargeStatistics>('/v1/charges/statistics');
  }
}

// Charge filters interface
export interface ChargeFilters {
  status?: string;
  currency?: string;
  customerId?: number;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
}

// Charge statistics interface
export interface ChargeStatistics {
  totalCharges: number;
  successfulCharges: number;
  failedCharges: number;
  successRate: number;
}

// Create charge request interface
export interface CreateChargeRequest {
  amount: number;
  currency: string;
  customerId: number;
  paymentMethod?: string;
  description?: string;
  metadata?: string;
  idempotencyKey?: string;
}
