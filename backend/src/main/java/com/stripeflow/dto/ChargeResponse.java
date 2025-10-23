package com.stripeflow.dto;

import com.stripeflow.model.Charge;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for charge response
 */
public class ChargeResponse {
    
    private Long id;
    private BigDecimal amount;
    private String currency;
    private CustomerResponse customer;
    private String status;
    private String paymentMethod;
    private String description;
    private String metadata;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ChargeResponse() {}
    
    public ChargeResponse(Charge charge) {
        this.id = charge.getId();
        this.amount = charge.getAmount();
        this.currency = charge.getCurrency();
        this.status = charge.getStatus().name();
        this.paymentMethod = charge.getPaymentMethod();
        this.description = charge.getDescription();
        this.metadata = charge.getMetadata();
        this.idempotencyKey = charge.getIdempotencyKey();
        this.createdAt = charge.getCreatedAt();
        this.updatedAt = charge.getUpdatedAt();
        
        if (charge.getCustomer() != null) {
            this.customer = new CustomerResponse(charge.getCustomer());
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public CustomerResponse getCustomer() {
        return customer;
    }
    
    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}



