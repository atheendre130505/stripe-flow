-- Create audit tables for tracking changes

-- Audit table for customers
CREATE TABLE customer_audit (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit table for charges
CREATE TABLE charge_audit (
    id BIGSERIAL PRIMARY KEY,
    charge_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit table for refunds
CREATE TABLE refund_audit (
    id BIGSERIAL PRIMARY KEY,
    refund_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit table for subscriptions
CREATE TABLE subscription_audit (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for audit tables
CREATE INDEX idx_customer_audit_customer_id ON customer_audit(customer_id);
CREATE INDEX idx_customer_audit_changed_at ON customer_audit(changed_at);
CREATE INDEX idx_charge_audit_charge_id ON charge_audit(charge_id);
CREATE INDEX idx_charge_audit_changed_at ON charge_audit(changed_at);
CREATE INDEX idx_refund_audit_refund_id ON refund_audit(refund_id);
CREATE INDEX idx_refund_audit_changed_at ON refund_audit(changed_at);
CREATE INDEX idx_subscription_audit_subscription_id ON subscription_audit(subscription_id);
CREATE INDEX idx_subscription_audit_changed_at ON subscription_audit(changed_at);

-- Create audit triggers
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO customer_audit (customer_id, action, new_values, changed_at)
        VALUES (NEW.id, 'INSERT', to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO customer_audit (customer_id, action, old_values, new_values, changed_at)
        VALUES (NEW.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO customer_audit (customer_id, action, old_values, changed_at)
        VALUES (OLD.id, 'DELETE', to_jsonb(OLD), CURRENT_TIMESTAMP);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create audit triggers for all main tables
CREATE TRIGGER customers_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON customers
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Create similar triggers for other tables
CREATE OR REPLACE FUNCTION charge_audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO charge_audit (charge_id, action, new_values, changed_at)
        VALUES (NEW.id, 'INSERT', to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO charge_audit (charge_id, action, old_values, new_values, changed_at)
        VALUES (NEW.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO charge_audit (charge_id, action, old_values, changed_at)
        VALUES (OLD.id, 'DELETE', to_jsonb(OLD), CURRENT_TIMESTAMP);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER charges_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON charges
    FOR EACH ROW EXECUTE FUNCTION charge_audit_trigger_function();

