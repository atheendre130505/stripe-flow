package com.stripeflow.dto;

import com.stripeflow.model.Subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for subscription response
 */
public class SubscriptionResponse {
    
    private Long id;
    private CustomerResponse customer;
    private String planId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime trialStart;
    private LocalDateTime trialEnd;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public SubscriptionResponse() {}
    
    public SubscriptionResponse(Subscription subscription) {
        this.id = subscription.getId();
        this.planId = subscription.getPlanId();
        this.amount = subscription.getAmount();
        this.currency = subscription.getCurrency();
        this.status = subscription.getStatus().name();
        this.currentPeriodStart = subscription.getCurrentPeriodStart();
        this.currentPeriodEnd = subscription.getCurrentPeriodEnd();
        this.trialStart = subscription.getTrialStart();
        this.trialEnd = subscription.getTrialEnd();
        this.description = subscription.getDescription();
        this.createdAt = subscription.getCreatedAt();
        this.updatedAt = subscription.getUpdatedAt();
        
        if (subscription.getCustomer() != null) {
            this.customer = new CustomerResponse(subscription.getCustomer());
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CustomerResponse getCustomer() {
        return customer;
    }
    
    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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



