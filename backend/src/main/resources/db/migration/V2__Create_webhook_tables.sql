-- Create webhook_endpoints table
CREATE TABLE webhook_endpoints (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    secret VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create webhook_events table
CREATE TABLE webhook_events (
    id BIGSERIAL PRIMARY KEY,
    endpoint_id BIGINT NOT NULL REFERENCES webhook_endpoints(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    event_data TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    last_attempt TIMESTAMP,
    next_retry TIMESTAMP,
    response_code INTEGER,
    response_body TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create api_keys table
CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    key_hash VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_used TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create webhook_event_types table for supported event types
CREATE TABLE webhook_event_types (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default webhook event types
INSERT INTO webhook_event_types (event_type, description) VALUES
('charge.succeeded', 'A charge has been successfully completed'),
('charge.failed', 'A charge has failed'),
('charge.pending', 'A charge is pending'),
('charge.canceled', 'A charge has been canceled'),
('refund.created', 'A refund has been created'),
('refund.succeeded', 'A refund has been successfully processed'),
('refund.failed', 'A refund has failed'),
('subscription.created', 'A subscription has been created'),
('subscription.updated', 'A subscription has been updated'),
('subscription.canceled', 'A subscription has been canceled'),
('customer.created', 'A customer has been created'),
('customer.updated', 'A customer has been updated');

-- Create indexes for webhook_events table
CREATE INDEX idx_webhook_events_endpoint_id ON webhook_events(endpoint_id);
CREATE INDEX idx_webhook_events_status ON webhook_events(status);
CREATE INDEX idx_webhook_events_next_retry ON webhook_events(next_retry);
CREATE INDEX idx_webhook_events_created_at ON webhook_events(created_at);
CREATE INDEX idx_webhook_events_event_type ON webhook_events(event_type);

-- Create indexes for webhook_endpoints table
CREATE INDEX idx_webhook_endpoints_enabled ON webhook_endpoints(enabled);
CREATE INDEX idx_webhook_endpoints_url ON webhook_endpoints(url);

-- Create indexes for api_keys table
CREATE INDEX idx_api_keys_key_hash ON api_keys(key_hash);
CREATE INDEX idx_api_keys_enabled ON api_keys(enabled);
CREATE INDEX idx_api_keys_last_used ON api_keys(last_used);

-- Create indexes for webhook_event_types table
CREATE INDEX idx_webhook_event_types_enabled ON webhook_event_types(enabled);
CREATE INDEX idx_webhook_event_types_event_type ON webhook_event_types(event_type);

-- Create triggers for updated_at
CREATE TRIGGER update_webhook_endpoints_updated_at BEFORE UPDATE ON webhook_endpoints
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_webhook_events_updated_at BEFORE UPDATE ON webhook_events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_api_keys_updated_at BEFORE UPDATE ON api_keys
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();



