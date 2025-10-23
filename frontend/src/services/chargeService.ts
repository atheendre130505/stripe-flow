import { apiService } from './api';
import { Charge, CreateChargeRequest, ChargeFilters, ChargeStatistics } from '../types';

export class ChargeService {
  // Get all charges with pagination and filters
  async getCharges(
    page: number = 1,
    limit: number = 20,
    filters?: ChargeFilters
  ): Promise<{
    data: Charge[];
    pagination: {
      page: number;
      limit: number;
      total: number;
      totalPages: number;
    };
  }> {
    const params = {
      page,
      limit,
      ...filters,
    };
    return apiService.get('/charges', params);
  }

  // Get charge by ID
  async getCharge(id: string): Promise<Charge> {
    return apiService.get(`/charges/${id}`);
  }

  // Create new charge
  async createCharge(data: CreateChargeRequest): Promise<Charge> {
    return apiService.post('/charges', data);
  }

  // Update charge
  async updateCharge(id: string, data: Partial<Charge>): Promise<Charge> {
    return apiService.put(`/charges/${id}`, data);
  }

  // Cancel charge
  async cancelCharge(id: string): Promise<Charge> {
    return apiService.post(`/charges/${id}/cancel`);
  }

  // Get charge statistics
  async getChargeStatistics(filters?: ChargeFilters): Promise<ChargeStatistics> {
    return apiService.get('/charges/statistics', filters);
  }

  // Get charges by customer
  async getChargesByCustomer(
    customerId: string,
    page: number = 1,
    limit: number = 20
  ): Promise<{
    data: Charge[];
    pagination: {
      page: number;
      limit: number;
      total: number;
      totalPages: number;
    };
  }> {
    return apiService.get(`/customers/${customerId}/charges`, { page, limit });
  }

  // Search charges
  async searchCharges(
    query: string,
    page: number = 1,
    limit: number = 20
  ): Promise<{
    data: Charge[];
    pagination: {
      page: number;
      limit: number;
      total: number;
      totalPages: number;
    };
  }> {
    return apiService.get('/charges/search', { q: query, page, limit });
  }

  // Export charges
  async exportCharges(filters?: ChargeFilters): Promise<Blob> {
    const response = await apiService.get('/charges/export', {
      ...filters,
      format: 'csv',
    });
    return new Blob([response as BlobPart], { type: 'text/csv' });
  }
}

export const chargeService = new ChargeService();
export default chargeService;