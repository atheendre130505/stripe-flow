import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { toast } from 'react-hot-toast';

// Types
interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// API Configuration
const API_BASE_URL = (import.meta as any).env?.VITE_API_URL || 'http://localhost:8080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        // Add auth token if available
        const token = localStorage.getItem('auth_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      (error) => {
        const message = this.getErrorMessage(error);
        
        // Show error toast for non-401 errors
        if (error.response?.status !== 401) {
          toast.error(message);
        }

        // Handle 401 errors (unauthorized)
        if (error.response?.status === 401) {
          localStorage.removeItem('auth_token');
          window.location.href = '/login';
        }

        return Promise.reject(error);
      }
    );
  }

  private getErrorMessage(error: any): string {
    if (typeof error === 'string') return error;
    if (error?.message) return error.message;
    if (error?.response?.data?.message) return error.response.data.message;
    if (error?.response?.data?.error) return error.response.data.error;
    return 'An unexpected error occurred';
  }

  // Generic methods
  async get<T>(url: string, params?: Record<string, any>): Promise<T> {
    const response = await this.api.get(url, { params });
    return response.data;
  }

  async post<T>(url: string, data?: any): Promise<T> {
    const response = await this.api.post(url, data);
    return response.data;
  }

  async put<T>(url: string, data?: any): Promise<T> {
    const response = await this.api.put(url, data);
    return response.data;
  }

  async patch<T>(url: string, data?: any): Promise<T> {
    const response = await this.api.patch(url, data);
    return response.data;
  }

  async delete<T>(url: string): Promise<T> {
    const response = await this.api.delete(url);
    return response.data;
  }

  // Health check
  async healthCheck(): Promise<{ status: string; timestamp: string }> {
    return this.get('/health');
  }

  // Dashboard stats
  async getDashboardStats(): Promise<{
    totalRevenue: number;
    totalTransactions: number;
    successRate: number;
    averageTransactionValue: number;
    monthlyRevenue: number;
    dailyTransactions: number;
  }> {
    return this.get('/dashboard/stats');
  }

  // Revenue chart data
  async getRevenueChartData(period: string = '30d'): Promise<Array<{
    date: string;
    revenue: number;
    transactions: number;
  }>> {
    return this.get('/dashboard/revenue-chart', { period });
  }

  // Top customers
  async getTopCustomers(limit: number = 10): Promise<Array<{
    customer: any;
    totalSpent: number;
    transactionCount: number;
  }>> {
    return this.get('/dashboard/top-customers', { limit });
  }
}

// Create singleton instance
export const apiService = new ApiService();

// Export individual methods for convenience
export const {
  get,
  post,
  put,
  patch,
  delete: del,
  healthCheck,
  getDashboardStats,
  getRevenueChartData,
  getTopCustomers,
} = apiService;

export default apiService;