-- Database performance optimization for high throughput
-- Target: 1000+ transactions per second with <100ms response times

-- Optimize customers table
CREATE INDEX CONCURRENTLY idx_customers_email_hash ON customers USING hash (email);
CREATE INDEX CONCURRENTLY idx_customers_name_gin ON customers USING gin (to_tsvector('english', name));
CREATE INDEX CONCURRENTLY idx_customers_created_at_btree ON customers (created_at DESC);
CREATE INDEX CONCURRENTLY idx_customers_phone ON customers (phone) WHERE phone IS NOT NULL;

-- Optimize charges table for high-frequency queries
CREATE INDEX CONCURRENTLY idx_charges_customer_status_created ON charges (customer_id, status, created_at DESC);
CREATE INDEX CONCURRENTLY idx_charges_status_currency_amount ON charges (status, currency, amount);
CREATE INDEX CONCURRENTLY idx_charges_created_at_btree ON charges (created_at DESC);
CREATE INDEX CONCURRENTLY idx_charges_amount_btree ON charges (amount);
CREATE INDEX CONCURRENTLY idx_charges_payment_method ON charges (payment_method) WHERE payment_method IS NOT NULL;

-- Optimize refunds table
CREATE INDEX CONCURRENTLY idx_refunds_charge_status_created ON refunds (charge_id, status, created_at DESC);
CREATE INDEX CONCURRENTLY idx_refunds_status_amount ON refunds (status, amount);
CREATE INDEX CONCURRENTLY idx_refunds_created_at_btree ON refunds (created_at DESC);

-- Optimize subscriptions table
CREATE INDEX CONCURRENTLY idx_subscriptions_customer_status_period ON subscriptions (customer_id, status, current_period_end);
CREATE INDEX CONCURRENTLY idx_subscriptions_plan_status ON subscriptions (plan_id, status);
CREATE INDEX CONCURRENTLY idx_subscriptions_status_period_end ON subscriptions (status, current_period_end);
CREATE INDEX CONCURRENTLY idx_subscriptions_currency_amount ON subscriptions (currency, amount);

-- Optimize webhook tables
CREATE INDEX CONCURRENTLY idx_webhook_endpoints_url_hash ON webhook_endpoints USING hash (url);
CREATE INDEX CONCURRENTLY idx_webhook_endpoints_enabled_created ON webhook_endpoints (enabled, created_at DESC);

CREATE INDEX CONCURRENTLY idx_webhook_events_endpoint_status_created ON webhook_events (endpoint_id, status, created_at DESC);
CREATE INDEX CONCURRENTLY idx_webhook_events_status_retry ON webhook_events (status, retry_count, next_retry);
CREATE INDEX CONCURRENTLY idx_webhook_events_type_created ON webhook_events (event_type, created_at DESC);
CREATE INDEX CONCURRENTLY idx_webhook_events_response_code ON webhook_events (response_code) WHERE response_code IS NOT NULL;

-- Optimize API keys table
CREATE INDEX CONCURRENTLY idx_api_keys_hash ON api_keys USING hash (key_hash);
CREATE INDEX CONCURRENTLY idx_api_keys_enabled_last_used ON api_keys (enabled, last_used DESC);

-- Create partial indexes for active records
CREATE INDEX CONCURRENTLY idx_charges_active_pending ON charges (id, created_at) WHERE status IN ('PENDING', 'PROCESSING');
CREATE INDEX CONCURRENTLY idx_subscriptions_active ON subscriptions (id, current_period_end) WHERE status = 'ACTIVE';
CREATE INDEX CONCURRENTLY idx_webhook_events_pending_retry ON webhook_events (id, next_retry) WHERE status = 'PENDING';

-- Create composite indexes for analytics queries
CREATE INDEX CONCURRENTLY idx_charges_analytics_daily ON charges (DATE(created_at), currency, status);
CREATE INDEX CONCURRENTLY idx_charges_analytics_monthly ON charges (DATE_TRUNC('month', created_at), currency, status);
CREATE INDEX CONCURRENTLY idx_subscriptions_analytics_daily ON subscriptions (DATE(created_at), currency, status);
CREATE INDEX CONCURRENTLY idx_refunds_analytics_daily ON refunds (DATE(created_at), status);

-- Create indexes for time-series queries
CREATE INDEX CONCURRENTLY idx_charges_timeseries ON charges (created_at, status, amount);
CREATE INDEX CONCURRENTLY idx_webhook_events_timeseries ON webhook_events (created_at, status, endpoint_id);

-- Create indexes for search functionality
CREATE INDEX CONCURRENTLY idx_customers_search ON customers USING gin (to_tsvector('english', name || ' ' || COALESCE(email, '')));
CREATE INDEX CONCURRENTLY idx_charges_search ON charges USING gin (to_tsvector('english', COALESCE(description, '')));

-- Create indexes for foreign key constraints (if not already present)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_charges_customer_id_fk ON charges (customer_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refunds_charge_id_fk ON refunds (charge_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_subscriptions_customer_id_fk ON subscriptions (customer_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_webhook_events_endpoint_id_fk ON webhook_events (endpoint_id);

-- Create indexes for audit tables
CREATE INDEX CONCURRENTLY idx_customer_audit_customer_created ON customer_audit (customer_id, changed_at DESC);
CREATE INDEX CONCURRENTLY idx_charge_audit_charge_created ON charge_audit (charge_id, changed_at DESC);
CREATE INDEX CONCURRENTLY idx_refund_audit_refund_created ON refund_audit (refund_id, changed_at DESC);
CREATE INDEX CONCURRENTLY idx_subscription_audit_subscription_created ON subscription_audit (subscription_id, changed_at DESC);

-- Create indexes for cleanup operations
CREATE INDEX CONCURRENTLY idx_webhook_events_cleanup ON webhook_events (created_at) WHERE created_at < NOW() - INTERVAL '30 days';
CREATE INDEX CONCURRENTLY idx_audit_cleanup ON customer_audit (changed_at) WHERE changed_at < NOW() - INTERVAL '1 year';

-- Update table statistics for better query planning
ANALYZE customers;
ANALYZE charges;
ANALYZE refunds;
ANALYZE subscriptions;
ANALYZE webhook_endpoints;
ANALYZE webhook_events;
ANALYZE api_keys;
ANALYZE customer_audit;
ANALYZE charge_audit;
ANALYZE refund_audit;
ANALYZE subscription_audit;

-- Create materialized views for complex analytics queries
CREATE MATERIALIZED VIEW mv_charge_statistics AS
SELECT 
    DATE(created_at) as date,
    currency,
    status,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM charges
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(created_at), currency, status;

CREATE UNIQUE INDEX idx_mv_charge_statistics_unique ON mv_charge_statistics (date, currency, status);

-- Create materialized view for webhook statistics
CREATE MATERIALIZED VIEW mv_webhook_statistics AS
SELECT 
    DATE(created_at) as date,
    endpoint_id,
    status,
    COUNT(*) as count,
    AVG(EXTRACT(EPOCH FROM (updated_at - created_at))) as avg_delivery_time
FROM webhook_events
WHERE created_at >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY DATE(created_at), endpoint_id, status;

CREATE UNIQUE INDEX idx_mv_webhook_statistics_unique ON mv_webhook_statistics (date, endpoint_id, status);

-- Create function to refresh materialized views
CREATE OR REPLACE FUNCTION refresh_analytics_views()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_charge_statistics;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_webhook_statistics;
END;
$$ LANGUAGE plpgsql;

-- Create scheduled job to refresh materialized views (requires pg_cron extension)
-- SELECT cron.schedule('refresh-analytics', '0 */5 * * *', 'SELECT refresh_analytics_views();');

-- Create function for efficient pagination
CREATE OR REPLACE FUNCTION get_charges_paginated(
    p_customer_id BIGINT DEFAULT NULL,
    p_status VARCHAR DEFAULT NULL,
    p_currency VARCHAR DEFAULT NULL,
    p_offset INTEGER DEFAULT 0,
    p_limit INTEGER DEFAULT 20
)
RETURNS TABLE (
    id BIGINT,
    amount INTEGER,
    currency VARCHAR,
    status VARCHAR,
    customer_id BIGINT,
    created_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT c.id, c.amount, c.currency, c.status, c.customer_id, c.created_at
    FROM charges c
    WHERE (p_customer_id IS NULL OR c.customer_id = p_customer_id)
      AND (p_status IS NULL OR c.status = p_status)
      AND (p_currency IS NULL OR c.currency = p_currency)
    ORDER BY c.created_at DESC
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- Create function for efficient search
CREATE OR REPLACE FUNCTION search_customers(
    p_search_term TEXT,
    p_offset INTEGER DEFAULT 0,
    p_limit INTEGER DEFAULT 20
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR,
    email VARCHAR,
    phone VARCHAR,
    created_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT c.id, c.name, c.email, c.phone, c.created_at
    FROM customers c
    WHERE to_tsvector('english', c.name || ' ' || COALESCE(c.email, '')) @@ plainto_tsquery('english', p_search_term)
    ORDER BY c.created_at DESC
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;
