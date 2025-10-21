// Customer types
export interface Customer {
  id: number
  email: string
  name: string
  phone?: string
  address?: Address
  createdAt: string
  updatedAt: string
}

export interface Address {
  line1?: string
  line2?: string
  city?: string
  state?: string
  postalCode?: string
  country?: string
}

// Charge types
export interface Charge {
  id: number
  amount: number
  currency: string
  customer: Customer
  status: ChargeStatus
  paymentMethod?: string
  description?: string
  metadata?: string
  idempotencyKey?: string
  createdAt: string
  updatedAt: string
}

export enum ChargeStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SUCCEEDED = 'SUCCEEDED',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED'
}

// Refund types
export interface Refund {
  id: number
  charge: Charge
  amount: number
  status: RefundStatus
  reason?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export enum RefundStatus {
  PENDING = 'PENDING',
  SUCCEEDED = 'SUCCEEDED',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED'
}

// Subscription types
export interface Subscription {
  id: number
  customer: Customer
  planId: string
  amount: number
  currency: string
  status: SubscriptionStatus
  currentPeriodStart: string
  currentPeriodEnd: string
  trialStart?: string
  trialEnd?: string
  description?: string
  createdAt: string
  updatedAt: string
}

export enum SubscriptionStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  CANCELED = 'CANCELED',
  PAST_DUE = 'PAST_DUE',
  UNPAID = 'UNPAID'
}

// API Response types
export interface ApiResponse<T> {
  data: T
  message?: string
  success: boolean
}

export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  limit: number
  totalPages: number
}

// Webhook types
export interface WebhookEndpoint {
  id: number
  url: string
  secret?: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

export interface WebhookEvent {
  id: number
  endpointId: number
  eventType: string
  eventData: string
  status: WebhookEventStatus
  retryCount: number
  lastAttempt?: string
  nextRetry?: string
  createdAt: string
  updatedAt: string
}

export enum WebhookEventStatus {
  PENDING = 'PENDING',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  RETRYING = 'RETRYING'
}

// API Key types
export interface ApiKey {
  id: number
  keyHash: string
  name: string
  description?: string
  enabled: boolean
  lastUsed?: string
  createdAt: string
  updatedAt: string
}

// Dashboard types
export interface DashboardStats {
  totalRevenue: number
  totalTransactions: number
  totalCustomers: number
  successRate: number
  revenueChange: number
  transactionChange: number
  customerChange: number
  successRateChange: number
}

