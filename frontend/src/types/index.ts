// Core Types
export interface User {
  id: string;
  email: string;
  name: string;
  role: 'admin' | 'user';
  createdAt: string;
  updatedAt: string;
}

// Customer Types
export interface Customer {
  id: string;
  name: string;
  email: string;
  phone?: string;
  address?: Address;
  createdAt: string;
  updatedAt: string;
  totalSpent: number;
  totalTransactions: number;
}

export interface Address {
  line1: string;
  line2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

export interface CreateCustomerRequest {
  name: string;
  email: string;
  phone?: string;
  address?: Address;
}

// Payment Types
export interface Charge {
  id: string;
  amount: number;
  currency: string;
  status: 'pending' | 'succeeded' | 'failed' | 'canceled';
  description?: string;
  customerId: string;
  customer?: Customer;
  paymentMethod: PaymentMethod;
  metadata?: Record<string, string>;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentMethod {
  id: string;
  type: 'card' | 'bank_account' | 'paypal';
  card?: {
    brand: string;
    last4: string;
    expMonth: number;
    expYear: number;
  };
  bankAccount?: {
    bankName: string;
    last4: string;
    routingNumber: string;
  };
}

export interface CreateChargeRequest {
  amount: number;
  currency: string;
  description?: string;
  customerId?: string;
  customer?: CreateCustomerRequest;
  paymentMethod: {
    type: 'card';
    card: {
      number: string;
      expMonth: number;
      expYear: number;
      cvc: string;
    };
  };
  metadata?: Record<string, string>;
}

// Refund Types
export interface Refund {
  id: string;
  amount: number;
  currency: string;
  status: 'pending' | 'succeeded' | 'failed' | 'canceled';
  reason?: string;
  chargeId: string;
  charge?: Charge;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRefundRequest {
  chargeId: string;
  amount?: number;
  reason?: string;
}

// Subscription Types
export interface Subscription {
  id: string;
  customerId: string;
  customer?: Customer;
  status: 'active' | 'canceled' | 'past_due' | 'unpaid' | 'incomplete';
  currentPeriodStart: string;
  currentPeriodEnd: string;
  cancelAtPeriodEnd: boolean;
  canceledAt?: string;
  plan: Plan;
  createdAt: string;
  updatedAt: string;
}

export interface Plan {
  id: string;
  name: string;
  description: string;
  amount: number;
  currency: string;
  interval: 'day' | 'week' | 'month' | 'year';
  intervalCount: number;
}

export interface CreateSubscriptionRequest {
  customerId: string;
  planId: string;
  trialPeriodDays?: number;
}

// Webhook Types
export interface WebhookEndpoint {
  id: string;
  url: string;
  events: string[];
  status: 'active' | 'inactive';
  secret: string;
  createdAt: string;
  updatedAt: string;
}

export interface WebhookEvent {
  id: string;
  type: string;
  data: Record<string, any>;
  status: 'pending' | 'delivered' | 'failed';
  attempts: number;
  lastAttemptAt?: string;
  endpointId: string;
  endpoint?: WebhookEndpoint;
  createdAt: string;
}

export interface CreateWebhookEndpointRequest {
  url: string;
  events: string[];
  secret?: string;
}

// Analytics Types
export interface DashboardStats {
  totalRevenue: number;
  totalTransactions: number;
  successRate: number;
  averageTransactionValue: number;
  monthlyRevenue: number;
  dailyTransactions: number;
}

export interface ChargeStatistics {
  totalCharges: number;
  successfulCharges: number;
  failedCharges: number;
  totalRevenue: number;
  averageAmount: number;
  successRate: number;
}

export interface RevenueChartData {
  date: string;
  revenue: number;
  transactions: number;
}

export interface TopCustomer {
  customer: Customer;
  totalSpent: number;
  transactionCount: number;
}

// API Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

export interface ApiError {
  message: string;
  code?: string;
  details?: Record<string, any>;
}

// Filter Types
export interface ChargeFilters {
  status?: string;
  customerId?: string;
  dateFrom?: string;
  dateTo?: string;
  amountMin?: number;
  amountMax?: number;
  search?: string;
}

export interface CustomerFilters {
  search?: string;
  createdFrom?: string;
  createdTo?: string;
}

// Form Types
export interface PaymentFormData {
  customerName: string;
  customerEmail: string;
  amount: number;
  currency: string;
  description: string;
  cardNumber: string;
  expiryMonth: string;
  expiryYear: string;
  cvc: string;
}

// UI Types
export interface TableColumn<T> {
  key: keyof T;
  title: string;
  render?: (value: any, item: T) => React.ReactNode;
  sortable?: boolean;
  width?: string;
}

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

// Chart Types
export interface ChartData {
  name: string;
  value: number;
  color?: string;
}

export interface TimeSeriesData {
  date: string;
  value: number;
}

// Notification Types
export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
}
