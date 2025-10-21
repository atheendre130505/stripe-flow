-- Performance optimization indexes for existing tables

-- Additional indexes for customers table
CREATE INDEX idx_customers_email_lower ON customers(LOWER(email));
CREATE INDEX idx_customers_name_lower ON customers(LOWER(name));
CREATE INDEX idx_customers_created_at_desc ON customers(created_at DESC);

-- Additional indexes for charges table
CREATE INDEX idx_charges_customer_id_status ON charges(customer_id, status);
CREATE INDEX idx_charges_currency_status ON charges(currency, status);
CREATE INDEX idx_charges_amount ON charges(amount);
CREATE INDEX idx_charges_created_at_desc ON charges(created_at DESC);
CREATE INDEX idx_charges_status_created_at ON charges(status, created_at);

-- Additional indexes for refunds table
CREATE INDEX idx_refunds_charge_id_status ON refunds(charge_id, status);
CREATE INDEX idx_refunds_status_created_at ON refunds(status, created_at);
CREATE INDEX idx_refunds_amount ON refunds(amount);

-- Additional indexes for subscriptions table
CREATE INDEX idx_subscriptions_customer_id_status ON subscriptions(customer_id, status);
CREATE INDEX idx_subscriptions_plan_id_status ON subscriptions(plan_id, status);
CREATE INDEX idx_subscriptions_currency ON subscriptions(currency);
CREATE INDEX idx_subscriptions_current_period_end ON subscriptions(current_period_end);
CREATE INDEX idx_subscriptions_status_period_end ON subscriptions(status, current_period_end);

-- Composite indexes for common queries
CREATE INDEX idx_charges_customer_created ON charges(customer_id, created_at DESC);
CREATE INDEX idx_charges_status_currency_created ON charges(status, currency, created_at DESC);
CREATE INDEX idx_subscriptions_customer_status ON subscriptions(customer_id, status);
CREATE INDEX idx_refunds_charge_status ON refunds(charge_id, status);

-- Partial indexes for active records
CREATE INDEX idx_charges_active ON charges(id) WHERE status IN ('PENDING', 'PROCESSING');
CREATE INDEX idx_subscriptions_active ON subscriptions(id) WHERE status = 'ACTIVE';
CREATE INDEX idx_webhook_events_pending ON webhook_events(id) WHERE status = 'PENDING';
CREATE INDEX idx_webhook_events_retrying ON webhook_events(id) WHERE status = 'RETRYING';

-- Indexes for analytics queries
CREATE INDEX idx_charges_analytics ON charges(currency, status, created_at);
CREATE INDEX idx_subscriptions_analytics ON subscriptions(currency, status, created_at);
CREATE INDEX idx_refunds_analytics ON refunds(charge_id, status, created_at);

