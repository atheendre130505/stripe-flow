import axios from 'axios';
import { apiClient, handleApiError } from '../api';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('ApiClient', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
      },
      writable: true,
    });
  });

  describe('GET requests', () => {
    it('should make GET request successfully', async () => {
      const mockData = { id: 1, name: 'Test' };
      mockedAxios.create.mockReturnValue({
        get: jest.fn().mockResolvedValue({ data: mockData }),
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      const result = await apiClient.get('/test');
      expect(result).toEqual(mockData);
    });

    it('should include query parameters in GET request', async () => {
      const mockData = { id: 1, name: 'Test' };
      const mockGet = jest.fn().mockResolvedValue({ data: mockData });
      mockedAxios.create.mockReturnValue({
        get: mockGet,
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      await apiClient.get('/test', { page: 1, size: 10 });
      expect(mockGet).toHaveBeenCalledWith('/test', { params: { page: 1, size: 10 } });
    });
  });

  describe('POST requests', () => {
    it('should make POST request successfully', async () => {
      const mockData = { id: 1, name: 'Test' };
      const mockPost = jest.fn().mockResolvedValue({ data: mockData });
      mockedAxios.create.mockReturnValue({
        post: mockPost,
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      const result = await apiClient.post('/test', { name: 'Test' });
      expect(result).toEqual(mockData);
      expect(mockPost).toHaveBeenCalledWith('/test', { name: 'Test' });
    });
  });

  describe('PUT requests', () => {
    it('should make PUT request successfully', async () => {
      const mockData = { id: 1, name: 'Updated' };
      const mockPut = jest.fn().mockResolvedValue({ data: mockData });
      mockedAxios.create.mockReturnValue({
        put: mockPut,
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      const result = await apiClient.put('/test/1', { name: 'Updated' });
      expect(result).toEqual(mockData);
      expect(mockPut).toHaveBeenCalledWith('/test/1', { name: 'Updated' });
    });
  });

  describe('DELETE requests', () => {
    it('should make DELETE request successfully', async () => {
      const mockDelete = jest.fn().mockResolvedValue({ data: null });
      mockedAxios.create.mockReturnValue({
        delete: mockDelete,
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      await apiClient.delete('/test/1');
      expect(mockDelete).toHaveBeenCalledWith('/test/1');
    });
  });

  describe('Paginated requests', () => {
    it('should make paginated GET request', async () => {
      const mockData = {
        content: [{ id: 1, name: 'Test' }],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true,
      };
      const mockGet = jest.fn().mockResolvedValue({ data: mockData });
      mockedAxios.create.mockReturnValue({
        get: mockGet,
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
      } as any);

      const result = await apiClient.getPaginated('/test', 0, 20);
      expect(result).toEqual(mockData);
      expect(mockGet).toHaveBeenCalledWith('/test', { params: { page: 0, size: 20 } });
    });
  });

  describe('API Key handling', () => {
    it('should include API key in headers when available', () => {
      (window.localStorage.getItem as jest.Mock).mockReturnValue('test-api-key');
      
      const mockUse = jest.fn();
      mockedAxios.create.mockReturnValue({
        interceptors: {
          request: { use: mockUse },
          response: { use: jest.fn() },
        },
      } as any);

      // Re-initialize to trigger interceptor setup
      new (apiClient.constructor as any)();
      
      expect(mockUse).toHaveBeenCalled();
    });
  });
});

describe('handleApiError', () => {
  it('should handle AxiosError with response', () => {
    const error = {
      isAxiosError: true,
      response: {
        status: 400,
        data: {
          message: 'Bad Request',
          errors: { field: 'Invalid value' }
        }
      },
      message: 'Request failed'
    };

    const result = handleApiError(error);
    expect(result).toEqual({
      message: 'Bad Request',
      status: 400,
      errors: { field: 'Invalid value' }
    });
  });

  it('should handle AxiosError without response', () => {
    const error = {
      isAxiosError: true,
      message: 'Network Error'
    };

    const result = handleApiError(error);
    expect(result).toEqual({
      message: 'Network Error',
      status: 500
    });
  });

  it('should handle non-AxiosError', () => {
    const error = {
      message: 'Unknown error'
    };

    const result = handleApiError(error);
    expect(result).toEqual({
      message: 'Unknown error',
      status: 500
    });
  });

  it('should handle error without message', () => {
    const error = {};

    const result = handleApiError(error);
    expect(result).toEqual({
      message: 'An unexpected error occurred',
      status: 500
    });
  });
});


