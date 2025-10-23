import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import { toast } from 'react-hot-toast';

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// API Error interface
export interface ApiError {
  message: string;
  status: number;
  errors?: Record<string, string>;
}

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
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
    this.client.interceptors.request.use(
      (config) => {
        // Add API key if available
        const apiKey = localStorage.getItem('apiKey');
        if (apiKey) {
          config.headers['X-API-Key'] = apiKey;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      (error: AxiosError) => {
        this.handleError(error);
        return Promise.reject(error);
      }
    );
  }

  private handleError(error: AxiosError) {
    const status = error.response?.status;
    const message = error.response?.data?.message || error.message;

    switch (status) {
      case 400:
        toast.error('Invalid request. Please check your input.');
        break;
      case 401:
        toast.error('Unauthorized. Please check your API key.');
        break;
      case 403:
        toast.error('Forbidden. You do not have permission to access this resource.');
        break;
      case 404:
        toast.error('Resource not found.');
        break;
      case 409:
        toast.error('Conflict. Resource already exists.');
        break;
      case 422:
        toast.error('Validation error. Please check your input.');
        break;
      case 500:
        toast.error('Server error. Please try again later.');
        break;
      default:
        toast.error(message || 'An unexpected error occurred.');
    }
  }

  // Generic HTTP methods
  async get<T>(url: string, params?: Record<string, any>): Promise<T> {
    const response = await this.client.get<T>(url, { params });
    return response.data;
  }

  async post<T>(url: string, data?: any): Promise<T> {
    const response = await this.client.post<T>(url, data);
    return response.data;
  }

  async put<T>(url: string, data?: any): Promise<T> {
    const response = await this.client.put<T>(url, data);
    return response.data;
  }

  async delete<T>(url: string): Promise<T> {
    const response = await this.client.delete<T>(url);
    return response.data;
  }

  // Paginated requests
  async getPaginated<T>(
    url: string,
    page: number = 0,
    size: number = 20,
    params?: Record<string, any>
  ): Promise<PaginatedResponse<T>> {
    const response = await this.get<PaginatedResponse<T>>(url, {
      page,
      size,
      ...params,
    });
    return response;
  }
}

// Export singleton instance
export const apiClient = new ApiClient();

// Export error handler for manual use
export const handleApiError = (error: any): ApiError => {
  if (axios.isAxiosError(error)) {
    return {
      message: error.response?.data?.message || error.message,
      status: error.response?.status || 500,
      errors: error.response?.data?.errors,
    };
  }
  return {
    message: error.message || 'An unexpected error occurred',
    status: 500,
  };
};


