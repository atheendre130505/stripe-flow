package com.stripeflow.service;

import com.stripeflow.dto.ChargeResponse;
import com.stripeflow.dto.CreateChargeRequest;
import com.stripeflow.model.Charge;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.ChargeRepository;
import com.stripeflow.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for charge operations
 */
@Service
@Transactional
public class ChargeService {
    
    @Autowired
    private ChargeRepository chargeRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Create a new charge
     */
    public ChargeResponse createCharge(CreateChargeRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + request.getCustomerId()));
        
        // Check for idempotency
        if (request.getIdempotencyKey() != null) {
            if (chargeRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
                Charge existingCharge = chargeRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .orElseThrow(() -> new IllegalStateException("Charge with idempotency key already exists"));
                return new ChargeResponse(existingCharge);
            }
        } else {
            // Generate idempotency key if not provided
            request.setIdempotencyKey(UUID.randomUUID().toString());
        }
        
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Charge amount must be greater than zero");
        }
        
        // Create charge
        Charge charge = new Charge();
        charge.setAmount(request.getAmount());
        charge.setCurrency(request.getCurrency());
        charge.setCustomer(customer);
        charge.setPaymentMethod(request.getPaymentMethod());
        charge.setDescription(request.getDescription());
        charge.setMetadata(request.getMetadata());
        charge.setIdempotencyKey(request.getIdempotencyKey());
        charge.setStatus(Charge.ChargeStatus.PENDING);
        
        // Simulate payment processing
        Charge savedCharge = chargeRepository.save(charge);
        
        // Process payment (simulate)
        processPayment(savedCharge);
        
        return new ChargeResponse(savedCharge);
    }
    
    /**
     * Get charge by ID
     */
    @Transactional(readOnly = true)
    public ChargeResponse getChargeById(Long id) {
        Charge charge = chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + id));
        return new ChargeResponse(charge);
    }
    
    /**
     * Get all charges with pagination
     */
    @Transactional(readOnly = true)
    public Page<ChargeResponse> getAllCharges(Pageable pageable) {
        return chargeRepository.findAll(pageable)
            .map(ChargeResponse::new);
    }
    
    /**
     * Get charges by customer
     */
    @Transactional(readOnly = true)
    public Page<ChargeResponse> getChargesByCustomer(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        
        return chargeRepository.findByCustomer(customer, pageable)
            .map(ChargeResponse::new);
    }
    
    /**
     * Get charges by status
     */
    @Transactional(readOnly = true)
    public Page<ChargeResponse> getChargesByStatus(Charge.ChargeStatus status, Pageable pageable) {
        return chargeRepository.findByStatus(status, pageable)
            .map(ChargeResponse::new);
    }
    
    /**
     * Get charges by currency
     */
    @Transactional(readOnly = true)
    public Page<ChargeResponse> getChargesByCurrency(String currency, Pageable pageable) {
        return chargeRepository.findByCurrency(currency, pageable)
            .map(ChargeResponse::new);
    }
    
    /**
     * Get recent charges
     */
    @Transactional(readOnly = true)
    public Page<ChargeResponse> getRecentCharges(Pageable pageable) {
        return chargeRepository.findRecentCharges(pageable)
            .map(ChargeResponse::new);
    }
    
    /**
     * Update charge status
     */
    public ChargeResponse updateChargeStatus(Long id, Charge.ChargeStatus status) {
        Charge charge = chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + id));
        
        charge.setStatus(status);
        Charge updatedCharge = chargeRepository.save(charge);
        return new ChargeResponse(updatedCharge);
    }
    
    /**
     * Cancel charge
     */
    public ChargeResponse cancelCharge(Long id) {
        Charge charge = chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + id));
        
        if (charge.getStatus() != Charge.ChargeStatus.PENDING && 
            charge.getStatus() != Charge.ChargeStatus.PROCESSING) {
            throw new IllegalStateException("Cannot cancel charge with status: " + charge.getStatus());
        }
        
        charge.setStatus(Charge.ChargeStatus.CANCELED);
        Charge updatedCharge = chargeRepository.save(charge);
        return new ChargeResponse(updatedCharge);
    }
    
    /**
     * Get charge statistics
     */
    @Transactional(readOnly = true)
    public ChargeStatistics getChargeStatistics() {
        long totalCharges = chargeRepository.count();
        long successfulCharges = chargeRepository.countChargesByStatus(Charge.ChargeStatus.SUCCEEDED);
        long failedCharges = chargeRepository.countChargesByStatus(Charge.ChargeStatus.FAILED);
        
        return new ChargeStatistics(totalCharges, successfulCharges, failedCharges);
    }
    
    /**
     * Get charge entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Charge getChargeEntityById(Long id) {
        return chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found with ID: " + id));
    }
    
    /**
     * Process payment (simulate)
     */
    private void processPayment(Charge charge) {
        // Simulate payment processing delay
        try {
            Thread.sleep(100); // 100ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate payment success/failure (90% success rate)
        if (Math.random() > 0.1) {
            charge.setStatus(Charge.ChargeStatus.SUCCEEDED);
        } else {
            charge.setStatus(Charge.ChargeStatus.FAILED);
        }
        
        chargeRepository.save(charge);
    }
    
    /**
     * Charge statistics inner class
     */
    public static class ChargeStatistics {
        private final long totalCharges;
        private final long successfulCharges;
        private final long failedCharges;
        
        public ChargeStatistics(long totalCharges, long successfulCharges, long failedCharges) {
            this.totalCharges = totalCharges;
            this.successfulCharges = successfulCharges;
            this.failedCharges = failedCharges;
        }
        
        public long getTotalCharges() { return totalCharges; }
        public long getSuccessfulCharges() { return successfulCharges; }
        public long getFailedCharges() { return failedCharges; }
        public double getSuccessRate() { 
            return totalCharges > 0 ? (double) successfulCharges / totalCharges * 100 : 0; 
        }
    }
}



