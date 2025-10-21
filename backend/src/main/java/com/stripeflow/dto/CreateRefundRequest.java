package com.stripeflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for creating a new refund
 */
public class CreateRefundRequest {
    
    @NotNull(message = "Charge ID is required")
    private Long chargeId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    // Constructors
    public CreateRefundRequest() {}
    
    public CreateRefundRequest(Long chargeId, BigDecimal amount) {
        this.chargeId = chargeId;
        this.amount = amount;
    }
    
    // Getters and Setters
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
}

