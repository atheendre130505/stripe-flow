import { rest } from 'msw';

export const handlers = [
  // Customer endpoints
  rest.get('/api/v1/customers', (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            name: 'John Doe',
            email: 'john@example.com',
            phone: '+1234567890',
            address: {
              line1: '123 Main St',
              city: 'New York',
              state: 'NY',
              postalCode: '10001',
              country: 'US'
            },
            createdAt: '2023-01-01T00:00:00Z',
            updatedAt: '2023-01-01T00:00:00Z'
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      })
    );
  }),

  rest.get('/api/v1/customers/:id', (req, res, ctx) => {
    const { id } = req.params;
    return res(
      ctx.json({
        id: parseInt(id as string),
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        address: {
          line1: '123 Main St',
          city: 'New York',
          state: 'NY',
          postalCode: '10001',
          country: 'US'
        },
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      })
    );
  }),

  rest.post('/api/v1/customers', (req, res, ctx) => {
    return res(
      ctx.status(201),
      ctx.json({
        id: 1,
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        address: {
          line1: '123 Main St',
          city: 'New York',
          state: 'NY',
          postalCode: '10001',
          country: 'US'
        },
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      })
    );
  }),

  // Charge endpoints
  rest.get('/api/v1/charges', (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            amount: 1000,
            currency: 'USD',
            status: 'SUCCEEDED',
            customer: {
              id: 1,
              name: 'John Doe',
              email: 'john@example.com'
            },
            description: 'Test charge',
            createdAt: '2023-01-01T00:00:00Z',
            updatedAt: '2023-01-01T00:00:00Z'
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      })
    );
  }),

  rest.get('/api/v1/charges/statistics', (req, res, ctx) => {
    return res(
      ctx.json({
        totalCharges: 100,
        successfulCharges: 95,
        failedCharges: 5,
        successRate: 95.0,
        totalRevenue: 10000
      })
    );
  }),

  // Webhook endpoints
  rest.get('/api/v1/webhooks/endpoints', (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            url: 'https://example.com/webhook',
            enabled: true,
            description: 'Test webhook',
            createdAt: '2023-01-01T00:00:00Z',
            updatedAt: '2023-01-01T00:00:00Z'
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      })
    );
  }),

  rest.get('/api/v1/webhooks/statistics', (req, res, ctx) => {
    return res(
      ctx.json({
        totalEndpoints: 5,
        enabledEndpoints: 3,
        totalEvents: 1000,
        pendingEvents: 10,
        deliveredEvents: 950,
        failedEvents: 40,
        deliverySuccessRate: 95.0
      })
    );
  }),

  rest.get('/api/v1/webhooks/events/recent', (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            endpointId: 1,
            eventType: 'charge.succeeded',
            eventData: '{"id": 1, "amount": 1000}',
            status: 'DELIVERED',
            retryCount: 0,
            maxRetries: 3,
            responseCode: 200,
            createdAt: '2023-01-01T00:00:00Z',
            updatedAt: '2023-01-01T00:00:00Z'
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true
      })
    );
  })
];
