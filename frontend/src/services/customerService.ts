import { apiClient, PaginatedResponse } from './api';
import { Customer, CreateCustomerRequest } from '@/types';

export class CustomerService {
  // Get all customers with pagination
  static async getCustomers(page: number = 0, size: number = 20, search?: string): Promise<PaginatedResponse<Customer>> {
    const params: Record<string, any> = { page, size };
    if (search) {
      params.name = search;
    }
    return apiClient.getPaginated<Customer>('/v1/customers', page, size, params);
  }

  // Get customer by ID
  static async getCustomer(id: number): Promise<Customer> {
    return apiClient.get<Customer>(`/v1/customers/${id}`);
  }

  // Create new customer
  static async createCustomer(customer: CreateCustomerRequest): Promise<Customer> {
    return apiClient.post<Customer>('/v1/customers', customer);
  }

  // Update customer
  static async updateCustomer(id: number, customer: CreateCustomerRequest): Promise<Customer> {
    return apiClient.put<Customer>(`/v1/customers/${id}`, customer);
  }

  // Delete customer
  static async deleteCustomer(id: number): Promise<void> {
    return apiClient.delete<void>(`/v1/customers/${id}`);
  }

  // Search customers by name
  static async searchCustomers(name: string, page: number = 0, size: number = 20): Promise<PaginatedResponse<Customer>> {
    return apiClient.getPaginated<Customer>('/v1/customers/search', page, size, { name });
  }
}

// Customer types
export interface CreateCustomerRequest {
  email: string;
  name: string;
  phone?: string;
  address?: {
    line1?: string;
    line2?: string;
    city?: string;
    state?: string;
    postalCode?: string;
    country?: string;
  };
}
