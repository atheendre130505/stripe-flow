# StripeFlow API Documentation

## Overview

The StripeFlow API is a comprehensive payment processing platform that provides secure, scalable, and reliable payment solutions. This documentation covers all available endpoints, authentication methods, and integration examples.

## Table of Contents

- [Getting Started](#getting-started)
- [Authentication](#authentication)
- [Rate Limiting](#rate-limiting)
- [API Endpoints](#api-endpoints)
- [Webhooks](#webhooks)
- [Error Handling](#error-handling)
- [SDKs and Examples](#sdks-and-examples)
- [Changelog](#changelog)

## Getting Started

### Base URL

- **Production**: `https://api.stripeflow.com`
- **Staging**: `https://api-staging.stripeflow.com`
- **Development**: `http://localhost:8080`

### API Version

All API endpoints are versioned. The current version is `v1`.

```
https://api.stripeflow.com/api/v1/
```

### Content Type

All requests must include the `Content-Type: application/json` header.

## Authentication

StripeFlow uses API key authentication. Include your API key in the `X-API-Key` header with every request.

```bash
curl -H "X-API-Key: your-api-key-here" \
     -H "Content-Type: application/json" \
     https://api.stripeflow.com/api/v1/charges
```

### Getting Your API Key

1. Log in to your StripeFlow dashboard
2. Navigate to **Settings** > **API Keys**
3. Click **Create New API Key**
4. Copy the generated key (it's only shown once)

### API Key Security

- Keep your API keys secure and never expose them in client-side code
- Use different API keys for different environments
- Rotate your API keys regularly
- Revoke compromised keys immediately

## Rate Limiting

API requests are rate limited to prevent abuse and ensure fair usage:

- **Rate Limit**: 100 requests per minute per API key
- **Burst Limit**: 200 requests per minute for short bursts
- **Headers**: Rate limit information is included in response headers

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

### Rate Limit Exceeded

When you exceed the rate limit, you'll receive a `429 Too Many Requests` response:

```json
{
  "timestamp": "2023-01-01T00:00:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 60 seconds.",
  "path": "/api/v1/charges"
}
```

## API Endpoints

### Customers

Manage customer information and payment methods.

#### Create Customer

```http
POST /api/v1/customers
```

**Request Body:**
```json
{
  "email": "customer@example.com",
  "name": "John Doe",
  "phone": "+1234567890",
  "address": {
    "line1": "123 Main St",
    "line2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "US"
  }
}
```

**Response:**
```json
{
  "id": 1,
  "email": "customer@example.com",
  "name": "John Doe",
  "phone": "+1234567890",
  "address": {
    "line1": "123 Main St",
    "line2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "US"
  },
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### Get Customer

```http
GET /api/v1/customers/{id}
```

**Response:**
```json
{
  "id": 1,
  "email": "customer@example.com",
  "name": "John Doe",
  "phone": "+1234567890",
  "address": {
    "line1": "123 Main St",
    "line2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "US"
  },
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### List Customers

```http
GET /api/v1/customers?page=0&size=20&search=john
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20, max: 100)
- `search` (optional): Search term for name or email

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "email": "customer@example.com",
      "name": "John Doe",
      "phone": "+1234567890",
      "createdAt": "2023-01-01T00:00:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "first": true,
  "last": true
}
```

### Charges

Process and manage payment charges.

#### Create Charge

```http
POST /api/v1/charges
```

**Request Body:**
```json
{
  "amount": 1000,
  "currency": "USD",
  "customerId": 1,
  "paymentMethod": "card_1234567890",
  "description": "Payment for services",
  "metadata": "order_12345"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 1000,
  "currency": "USD",
  "status": "SUCCEEDED",
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "customer@example.com"
  },
  "paymentMethod": "card_1234567890",
  "description": "Payment for services",
  "metadata": "order_12345",
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### Get Charge

```http
GET /api/v1/charges/{id}
```

#### List Charges

```http
GET /api/v1/charges?page=0&size=20&status=SUCCEEDED&currency=USD
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20, max: 100)
- `status` (optional): Filter by status (PENDING, SUCCEEDED, FAILED, CANCELED)
- `currency` (optional): Filter by currency (USD, EUR, GBP, etc.)
- `customerId` (optional): Filter by customer ID
- `startDate` (optional): Filter by start date (ISO 8601)
- `endDate` (optional): Filter by end date (ISO 8601)

#### Update Charge Status

```http
PUT /api/v1/charges/{id}/status?status=SUCCEEDED
```

#### Cancel Charge

```http
PUT /api/v1/charges/{id}/cancel
```

### Refunds

Process refunds for successful charges.

#### Create Refund

```http
POST /api/v1/refunds
```

**Request Body:**
```json
{
  "chargeId": 1,
  "amount": 500,
  "reason": "requested_by_customer",
  "description": "Partial refund for order cancellation"
}
```

**Response:**
```json
{
  "id": 1,
  "chargeId": 1,
  "amount": 500,
  "currency": "USD",
  "status": "SUCCEEDED",
  "reason": "requested_by_customer",
  "description": "Partial refund for order cancellation",
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### Get Refund

```http
GET /api/v1/refunds/{id}
```

#### List Refunds

```http
GET /api/v1/refunds?page=0&size=20&status=SUCCEEDED
```

### Subscriptions

Manage recurring payments and subscriptions.

#### Create Subscription

```http
POST /api/v1/subscriptions
```

**Request Body:**
```json
{
  "customerId": 1,
  "planId": "basic-monthly",
  "amount": 2900,
  "currency": "USD",
  "interval": "month",
  "intervalCount": 1,
  "trialPeriodDays": 7
}
```

**Response:**
```json
{
  "id": 1,
  "customerId": 1,
  "planId": "basic-monthly",
  "amount": 2900,
  "currency": "USD",
  "interval": "month",
  "intervalCount": 1,
  "status": "ACTIVE",
  "currentPeriodStart": "2023-01-01T00:00:00Z",
  "currentPeriodEnd": "2023-02-01T00:00:00Z",
  "trialStart": "2023-01-01T00:00:00Z",
  "trialEnd": "2023-01-08T00:00:00Z",
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### Get Subscription

```http
GET /api/v1/subscriptions/{id}
```

#### Update Subscription

```http
PUT /api/v1/subscriptions/{id}
```

#### Cancel Subscription

```http
PUT /api/v1/subscriptions/{id}/cancel
```

### Webhooks

Configure and manage webhook endpoints for real-time notifications.

#### Create Webhook Endpoint

```http
POST /api/v1/webhooks/endpoints
```

**Request Body:**
```json
{
  "url": "https://your-app.com/webhooks",
  "secret": "whsec_your_webhook_secret",
  "enabled": true,
  "description": "Production webhook endpoint"
}
```

**Response:**
```json
{
  "id": 1,
  "url": "https://your-app.com/webhooks",
  "enabled": true,
  "description": "Production webhook endpoint",
  "createdAt": "2023-01-01T00:00:00Z",
  "updatedAt": "2023-01-01T00:00:00Z"
}
```

#### List Webhook Endpoints

```http
GET /api/v1/webhooks/endpoints?page=0&size=20
```

#### Get Webhook Endpoint

```http
GET /api/v1/webhooks/endpoints/{id}
```

#### Update Webhook Endpoint

```http
PUT /api/v1/webhooks/endpoints/{id}
```

#### Delete Webhook Endpoint

```http
DELETE /api/v1/webhooks/endpoints/{id}
```

#### List Webhook Events

```http
GET /api/v1/webhooks/events/recent?page=0&size=20
```

#### Retry Webhook Event

```http
PUT /api/v1/webhooks/events/{id}/retry
```

### Analytics

Get insights and analytics about your payments.

#### Get Charge Statistics

```http
GET /api/v1/charges/statistics
```

**Response:**
```json
{
  "totalCharges": 1000,
  "successfulCharges": 950,
  "failedCharges": 50,
  "successRate": 95.0,
  "totalRevenue": 100000.00,
  "averageAmount": 100.00,
  "currencyBreakdown": {
    "USD": 800,
    "EUR": 150,
    "GBP": 50
  }
}
```

#### Get Webhook Statistics

```http
GET /api/v1/webhooks/statistics
```

**Response:**
```json
{
  "totalEndpoints": 5,
  "enabledEndpoints": 3,
  "totalEvents": 10000,
  "deliveredEvents": 9500,
  "failedEvents": 500,
  "deliverySuccessRate": 95.0,
  "averageDeliveryTime": 150.5
}
```

## Webhooks

Webhooks allow you to receive real-time notifications about events in your StripeFlow account.

### Supported Events

- `charge.succeeded` - A charge has been successfully completed
- `charge.failed` - A charge has failed
- `charge.pending` - A charge is pending
- `charge.canceled` - A charge has been canceled
- `refund.created` - A refund has been created
- `refund.succeeded` - A refund has been successfully processed
- `refund.failed` - A refund has failed
- `subscription.created` - A subscription has been created
- `subscription.updated` - A subscription has been updated
- `subscription.canceled` - A subscription has been canceled
- `customer.created` - A customer has been created
- `customer.updated` - A customer has been updated

### Webhook Payload

```json
{
  "id": "evt_1234567890",
  "type": "charge.succeeded",
  "data": {
    "object": {
      "id": "ch_1234567890",
      "amount": 1000,
      "currency": "usd",
      "status": "succeeded",
      "customer": {
        "id": 1,
        "name": "John Doe",
        "email": "customer@example.com"
      }
    }
  },
  "created": 1640995200,
  "livemode": false
}
```

### Signature Verification

All webhook requests include a signature header for verification:

```http
X-Webhook-Signature: sha256=signature_here
```

### Retry Logic

Webhooks are automatically retried with exponential backoff:
- 1st retry: 1 second
- 2nd retry: 5 seconds
- 3rd retry: 15 seconds
- 4th retry: 60 seconds

## Error Handling

The API uses standard HTTP status codes and returns detailed error information.

### Error Response Format

```json
{
  "timestamp": "2023-01-01T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "path": "/api/v1/charges",
  "errors": [
    {
      "field": "amount",
      "message": "Amount must be greater than 0",
      "rejectedValue": -100
    }
  ]
}
```

### Common Error Codes

- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Invalid or missing API key
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Validation errors
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

## SDKs and Examples

### cURL Examples

#### Create a Customer

```bash
curl -X POST https://api.stripeflow.com/api/v1/customers \
  -H "X-API-Key: your-api-key-here" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "name": "John Doe",
    "phone": "+1234567890"
  }'
```

#### Create a Charge

```bash
curl -X POST https://api.stripeflow.com/api/v1/charges \
  -H "X-API-Key: your-api-key-here" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000,
    "currency": "USD",
    "customerId": 1,
    "description": "Payment for services"
  }'
```

### JavaScript Example

```javascript
const stripeflow = require('stripeflow');

const client = new stripeflow.Client('your-api-key-here');

// Create a customer
const customer = await client.customers.create({
  email: 'customer@example.com',
  name: 'John Doe',
  phone: '+1234567890'
});

// Create a charge
const charge = await client.charges.create({
  amount: 1000,
  currency: 'USD',
  customerId: customer.id,
  description: 'Payment for services'
});
```

### Python Example

```python
import stripeflow

client = stripeflow.Client('your-api-key-here')

# Create a customer
customer = client.customers.create(
    email='customer@example.com',
    name='John Doe',
    phone='+1234567890'
)

# Create a charge
charge = client.charges.create(
    amount=1000,
    currency='USD',
    customer_id=customer.id,
    description='Payment for services'
)
```

## Changelog

### Version 1.0.0 (2023-01-01)

- Initial release
- Customer management
- Charge processing
- Refund processing
- Subscription management
- Webhook system
- Analytics and reporting

---

For more information, visit our [documentation website](https://docs.stripeflow.com) or contact support at support@stripeflow.com.
