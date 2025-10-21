package com.stripeflow.service;

import com.stripeflow.dto.CreateRefundRequest;
import com.stripeflow.dto.RefundResponse;
import com.stripeflow.model.Charge;
import com.stripeflow.model.Refund;
import com.stripeflow.repository.ChargeRepository;
import com.stripeflow.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for refund operations
 */
@Service
@Transactional
public class RefundService {
    
    @Autowired
    private RefundRepository refundRepository;
    
    @Autowired
    private ChargeRepository chargeRepository;
    
    /**
     * Create a new refund
     */
    public RefundResponse createRefund(CreateRefundRequest request) {
        // Validate charge exists and is successful
        Charge charge = chargeRepository.findById(request.getChargeId())
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + request.getChargeId()));
        
        if (charge.getStatus() != Charge.ChargeStatus.SUCCEEDED) {
            throw new IllegalStateException("Cannot refund charge with status: " + charge.getStatus());
        }
        
        // Validate refund amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be greater than zero");
        }
        
        // Check if refund amount exceeds charge amount
        BigDecimal totalRefunded = refundRepository.calculateTotalRefundedAmountByCharge(charge);
        BigDecimal remainingAmount = charge.getAmount().subtract(totalRefunded);
        
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds remaining charge amount. Remaining: " + remainingAmount);
        }
        
        // Create refund
        Refund refund = new Refund();
        refund.setCharge(charge);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setNotes(request.getNotes());
        refund.setStatus(Refund.RefundStatus.PENDING);
        
        Refund savedRefund = refundRepository.save(refund);
        
        // Process refund (simulate)
        processRefund(savedRefund);
        
        return new RefundResponse(savedRefund);
    }
    
    /**
     * Get refund by ID
     */
    @Transactional(readOnly = true)
    public RefundResponse getRefundById(Long id) {
        Refund refund = refundRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found with ID: " + id));
        return new RefundResponse(refund);
    }
    
    /**
     * Get all refunds with pagination
     */
    @Transactional(readOnly = true)
    public Page<RefundResponse> getAllRefunds(Pageable pageable) {
        return refundRepository.findAll(pageable)
            .map(RefundResponse::new);
    }
    
    /**
     * Get refunds by charge
     */
    @Transactional(readOnly = true)
    public Page<RefundResponse> getRefundsByCharge(Long chargeId, Pageable pageable) {
        Charge charge = chargeRepository.findById(chargeId)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + chargeId));
        
        return refundRepository.findByCharge(charge, pageable)
            .map(RefundResponse::new);
    }
    
    /**
     * Get refunds by status
     */
    @Transactional(readOnly = true)
    public Page<RefundResponse> getRefundsByStatus(Refund.RefundStatus status, Pageable pageable) {
        return refundRepository.findByStatus(status, pageable)
            .map(RefundResponse::new);
    }
    
    /**
     * Get recent refunds
     */
    @Transactional(readOnly = true)
    public Page<RefundResponse> getRecentRefunds(Pageable pageable) {
        return refundRepository.findRecentRefunds(pageable)
            .map(RefundResponse::new);
    }
    
    /**
     * Update refund status
     */
    public RefundResponse updateRefundStatus(Long id, Refund.RefundStatus status) {
        Refund refund = refundRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found with ID: " + id));
        
        refund.setStatus(status);
        Refund updatedRefund = refundRepository.save(refund);
        return new RefundResponse(updatedRefund);
    }
    
    /**
     * Cancel refund
     */
    public RefundResponse cancelRefund(Long id) {
        Refund refund = refundRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found with ID: " + id));
        
        if (refund.getStatus() != Refund.RefundStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel refund with status: " + refund.getStatus());
        }
        
        refund.setStatus(Refund.RefundStatus.CANCELED);
        Refund updatedRefund = refundRepository.save(refund);
        return new RefundResponse(updatedRefund);
    }
    
    /**
     * Get refund statistics
     */
    @Transactional(readOnly = true)
    public RefundStatistics getRefundStatistics() {
        long totalRefunds = refundRepository.count();
        long successfulRefunds = refundRepository.countRefundsByStatus(Refund.RefundStatus.SUCCEEDED);
        long failedRefunds = refundRepository.countRefundsByStatus(Refund.RefundStatus.FAILED);
        
        return new RefundStatistics(totalRefunds, successfulRefunds, failedRefunds);
    }
    
    /**
     * Get refund entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Refund getRefundEntityById(Long id) {
        return refundRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found with ID: " + id));
    }
    
    /**
     * Process refund (simulate)
     */
    private void processRefund(Refund refund) {
        // Simulate refund processing delay
        try {
            Thread.sleep(50); // 50ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate refund success/failure (95% success rate)
        if (Math.random() > 0.05) {
            refund.setStatus(Refund.RefundStatus.SUCCEEDED);
        } else {
            refund.setStatus(Refund.RefundStatus.FAILED);
        }
        
        refundRepository.save(refund);
    }
    
    /**
     * Refund statistics inner class
     */
    public static class RefundStatistics {
        private final long totalRefunds;
        private final long successfulRefunds;
        private final long failedRefunds;
        
        public RefundStatistics(long totalRefunds, long successfulRefunds, long failedRefunds) {
            this.totalRefunds = totalRefunds;
            this.successfulRefunds = successfulRefunds;
            this.failedRefunds = failedRefunds;
        }
        
        public long getTotalRefunds() { return totalRefunds; }
        public long getSuccessfulRefunds() { return successfulRefunds; }
        public long getFailedRefunds() { return failedRefunds; }
        public double getSuccessRate() { 
            return totalRefunds > 0 ? (double) successfulRefunds / totalRefunds * 100 : 0; 
        }
    }
}

