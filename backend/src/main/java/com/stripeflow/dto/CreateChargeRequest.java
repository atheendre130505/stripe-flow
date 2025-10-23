package com.stripeflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for creating a new charge
 */
public class CreateChargeRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    @Pattern(regexp = "USD|EUR|GBP|INR|JPY", message = "Currency must be one of: USD, EUR, GBP, INR, JPY")
    private String currency;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Size(max = 255, message = "Payment method must not exceed 255 characters")
    private String paymentMethod;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    @Size(max = 1000, message = "Metadata must not exceed 1000 characters")
    private String metadata;
    
    @Size(max = 255, message = "Idempotency key must not exceed 255 characters")
    private String idempotencyKey;
    
    // Constructors
    public CreateChargeRequest() {}
    
    public CreateChargeRequest(BigDecimal amount, String currency, Long customerId) {
        this.amount = amount;
        this.currency = currency;
        this.customerId = customerId;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
    
    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}



