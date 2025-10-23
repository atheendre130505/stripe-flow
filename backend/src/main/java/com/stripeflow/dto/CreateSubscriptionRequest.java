package com.stripeflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating a new subscription
 */
public class CreateSubscriptionRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Plan ID is required")
    @Size(max = 255, message = "Plan ID must not exceed 255 characters")
    private String planId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Current period start is required")
    private LocalDateTime currentPeriodStart;
    
    @NotNull(message = "Current period end is required")
    private LocalDateTime currentPeriodEnd;
    
    private LocalDateTime trialStart;
    
    private LocalDateTime trialEnd;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    // Constructors
    public CreateSubscriptionRequest() {}
    
    public CreateSubscriptionRequest(Long customerId, String planId, BigDecimal amount, String currency) {
        this.customerId = customerId;
        this.planId = planId;
        this.amount = amount;
        this.currency = currency;
    }
    
    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
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
    
    public LocalDateTime getCurrentPeriodStart() {
        return currentPeriodStart;
    }
    
    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }
    
    public LocalDateTime getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }
    
    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }
    
    public LocalDateTime getTrialStart() {
        return trialStart;
    }
    
    public void setTrialStart(LocalDateTime trialStart) {
        this.trialStart = trialStart;
    }
    
    public LocalDateTime getTrialEnd() {
        return trialEnd;
    }
    
    public void setTrialEnd(LocalDateTime trialEnd) {
        this.trialEnd = trialEnd;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}



