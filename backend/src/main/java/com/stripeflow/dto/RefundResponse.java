package com.stripeflow.dto;

import com.stripeflow.model.Refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for refund response
 */
public class RefundResponse {
    
    private Long id;
    private Long chargeId;
    private BigDecimal amount;
    private String status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public RefundResponse() {}
    
    public RefundResponse(Refund refund) {
        this.id = refund.getId();
        this.chargeId = refund.getCharge().getId();
        this.amount = refund.getAmount();
        this.status = refund.getStatus().name();
        this.reason = refund.getReason();
        this.notes = refund.getNotes();
        this.createdAt = refund.getCreatedAt();
        this.updatedAt = refund.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getChargeId() {
        return chargeId;
    }
    
    public void setChargeId(Long chargeId) {
        this.chargeId = chargeId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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

