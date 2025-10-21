-- Create monitoring views for database performance and webhook analytics

-- View for webhook delivery statistics
CREATE VIEW webhook_delivery_stats AS
SELECT 
    we.id as endpoint_id,
    we.url,
    we.enabled,
    COUNT(wev.id) as total_events,
    COUNT(CASE WHEN wev.status = 'DELIVERED' THEN 1 END) as delivered_events,
    COUNT(CASE WHEN wev.status = 'FAILED' THEN 1 END) as failed_events,
    COUNT(CASE WHEN wev.status = 'PENDING' THEN 1 END) as pending_events,
    ROUND(
        COUNT(CASE WHEN wev.status = 'DELIVERED' THEN 1 END) * 100.0 / 
        NULLIF(COUNT(wev.id), 0), 2
    ) as success_rate
FROM webhook_endpoints we
LEFT JOIN webhook_events wev ON we.id = wev.endpoint_id
GROUP BY we.id, we.url, we.enabled;

-- View for charge analytics
CREATE VIEW charge_analytics AS
SELECT 
    DATE(created_at) as charge_date,
    currency,
    status,
    COUNT(*) as charge_count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM charges
GROUP BY DATE(created_at), currency, status
ORDER BY charge_date DESC;

-- View for customer analytics
CREATE VIEW customer_analytics AS
SELECT 
    DATE(created_at) as customer_date,
    COUNT(*) as new_customers,
    COUNT(CASE WHEN id IN (
        SELECT DISTINCT customer_id FROM charges WHERE status = 'SUCCEEDED'
    ) THEN 1 END) as customers_with_successful_charges
FROM customers
GROUP BY DATE(created_at)
ORDER BY customer_date DESC;

-- View for subscription analytics
CREATE VIEW subscription_analytics AS
SELECT 
    status,
    currency,
    COUNT(*) as subscription_count,
    SUM(amount) as total_recurring_revenue,
    AVG(amount) as avg_subscription_amount
FROM subscriptions
GROUP BY status, currency
ORDER BY status, currency;

-- View for refund analytics
CREATE VIEW refund_analytics AS
SELECT 
    DATE(created_at) as refund_date,
    status,
    COUNT(*) as refund_count,
    SUM(amount) as total_refunded_amount
FROM refunds
GROUP BY DATE(created_at), status
ORDER BY refund_date DESC;

-- View for system health metrics
CREATE VIEW system_health_metrics AS
SELECT 
    'customers' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as last_7_days,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as last_30_days
FROM customers
UNION ALL
SELECT 
    'charges' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as last_7_days,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as last_30_days
FROM charges
UNION ALL
SELECT 
    'refunds' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as last_7_days,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as last_30_days
FROM refunds
UNION ALL
SELECT 
    'subscriptions' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as last_7_days,
    COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as last_30_days
FROM subscriptions;

-- Create indexes for monitoring queries
CREATE INDEX idx_charges_created_at_date ON charges(DATE(created_at));
CREATE INDEX idx_customers_created_at_date ON customers(DATE(created_at));
CREATE INDEX idx_refunds_created_at_date ON refunds(DATE(created_at));
CREATE INDEX idx_subscriptions_created_at_date ON subscriptions(DATE(created_at));

-- Create function to get webhook endpoint health
CREATE OR REPLACE FUNCTION get_webhook_endpoint_health(endpoint_id BIGINT)
RETURNS TABLE (
    endpoint_id BIGINT,
    url VARCHAR,
    enabled BOOLEAN,
    total_events BIGINT,
    delivered_events BIGINT,
    failed_events BIGINT,
    success_rate NUMERIC,
    last_event_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        we.id,
        we.url,
        we.enabled,
        COUNT(wev.id) as total_events,
        COUNT(CASE WHEN wev.status = 'DELIVERED' THEN 1 END) as delivered_events,
        COUNT(CASE WHEN wev.status = 'FAILED' THEN 1 END) as failed_events,
        ROUND(
            COUNT(CASE WHEN wev.status = 'DELIVERED' THEN 1 END) * 100.0 / 
            NULLIF(COUNT(wev.id), 0), 2
        ) as success_rate,
        MAX(wev.created_at) as last_event_at
    FROM webhook_endpoints we
    LEFT JOIN webhook_events wev ON we.id = wev.endpoint_id
    WHERE we.id = endpoint_id
    GROUP BY we.id, we.url, we.enabled;
END;
$$ LANGUAGE plpgsql;

-- Create function to get system performance metrics
CREATE OR REPLACE FUNCTION get_system_performance_metrics()
RETURNS TABLE (
    metric_name VARCHAR,
    metric_value NUMERIC,
    metric_unit VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        'total_customers'::VARCHAR,
        COUNT(*)::NUMERIC,
        'count'::VARCHAR
    FROM customers
    UNION ALL
    SELECT 
        'total_charges'::VARCHAR,
        COUNT(*)::NUMERIC,
        'count'::VARCHAR
    FROM charges
    UNION ALL
    SELECT 
        'successful_charges'::VARCHAR,
        COUNT(*)::NUMERIC,
        'count'::VARCHAR
    FROM charges WHERE status = 'SUCCEEDED'
    UNION ALL
    SELECT 
        'total_revenue'::VARCHAR,
        COALESCE(SUM(amount), 0)::NUMERIC,
        'currency'::VARCHAR
    FROM charges WHERE status = 'SUCCEEDED'
    UNION ALL
    SELECT 
        'active_subscriptions'::VARCHAR,
        COUNT(*)::NUMERIC,
        'count'::VARCHAR
    FROM subscriptions WHERE status = 'ACTIVE'
    UNION ALL
    SELECT 
        'webhook_endpoints'::VARCHAR,
        COUNT(*)::NUMERIC,
        'count'::VARCHAR
    FROM webhook_endpoints WHERE enabled = true;
END;
$$ LANGUAGE plpgsql;
