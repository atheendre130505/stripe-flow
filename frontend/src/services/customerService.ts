import { apiService } from './api';
import { Customer, CreateCustomerRequest, CustomerFilters } from '../types';

export class CustomerService {
  // Get all customers with pagination and filters
  async getCustomers(
    page: number = 1,
    limit: number = 20,
    filters?: CustomerFilters
  ): Promise<{
    data: Customer[];
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
    return apiService.get('/customers', params);
  }

  // Get customer by ID
  async getCustomer(id: string): Promise<Customer> {
    return apiService.get(`/customers/${id}`);
  }

  // Create new customer
  async createCustomer(data: CreateCustomerRequest): Promise<Customer> {
    return apiService.post('/customers', data);
  }

  // Update customer
  async updateCustomer(id: string, data: Partial<Customer>): Promise<Customer> {
    return apiService.put(`/customers/${id}`, data);
  }

  // Delete customer
  async deleteCustomer(id: string): Promise<void> {
    return apiService.delete(`/customers/${id}`);
  }

  // Search customers
  async searchCustomers(
    query: string,
    page: number = 1,
    limit: number = 20
  ): Promise<{
    data: Customer[];
    pagination: {
      page: number;
      limit: number;
      total: number;
      totalPages: number;
    };
  }> {
    return apiService.get('/customers/search', { q: query, page, limit });
  }

  // Get customer statistics
  async getCustomerStatistics(customerId: string): Promise<{
    totalSpent: number;
    totalTransactions: number;
    averageTransactionValue: number;
    lastTransactionDate: string;
    favoritePaymentMethod: string;
  }> {
    return apiService.get(`/customers/${customerId}/statistics`);
  }

  // Get customer's payment methods
  async getCustomerPaymentMethods(customerId: string): Promise<any[]> {
    return apiService.get(`/customers/${customerId}/payment-methods`);
  }

  // Export customers
  async exportCustomers(filters?: CustomerFilters): Promise<Blob> {
    const response = await apiService.get('/customers/export', {
      ...filters,
      format: 'csv',
    });
    return new Blob([response as BlobPart], { type: 'text/csv' });
  }
}

export const customerService = new CustomerService();
export default customerService;