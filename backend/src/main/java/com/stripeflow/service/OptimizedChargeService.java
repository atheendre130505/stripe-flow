package com.stripeflow.service;

import com.stripeflow.dto.ChargeResponse;
import com.stripeflow.dto.CreateChargeRequest;
import com.stripeflow.model.Charge;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.ChargeRepository;
import com.stripeflow.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * High-performance charge service optimized for 1000+ TPS
 */
@Service
@Transactional
public class OptimizedChargeService {
    
    @Autowired
    private ChargeRepository chargeRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private WebhookService webhookService;
    
    /**
     * Create charge with optimized processing
     */
    @Transactional
    public ChargeResponse createCharge(CreateChargeRequest request) {
        // Validate customer exists (with cache)
        Customer customer = getCachedCustomer(request.getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        // Create charge entity
        Charge charge = new Charge();
        charge.setAmount(request.getAmount());
        charge.setCurrency(request.getCurrency());
        charge.setCustomer(customer);
        charge.setPaymentMethod(request.getPaymentMethod());
        charge.setDescription(request.getDescription());
        charge.setMetadata(request.getMetadata());
        charge.setStatus(Charge.ChargeStatus.PENDING);
        charge.setCreatedAt(LocalDateTime.now());
        charge.setUpdatedAt(LocalDateTime.now());
        
        // Save charge
        Charge savedCharge = chargeRepository.save(charge);
        
        // Cache the charge
        cacheService.cacheCharge(savedCharge.getId(), savedCharge);
        
        // Process payment asynchronously
        processPaymentAsync(savedCharge);
        
        return new ChargeResponse(savedCharge);
    }
    
    /**
     * Get charge with cache optimization
     */
    @Cacheable(value = "charges", key = "#id")
    public ChargeResponse getCharge(Long id) {
        Charge charge = chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found"));
        return new ChargeResponse(charge);
    }
    
    /**
     * Get charges with optimized pagination
     */
    public Page<ChargeResponse> getCharges(Pageable pageable, String status, String currency, Long customerId) {
        // Try cache first for common queries
        String cacheKey = String.format("charges:%d:%s:%s:%d", 
            pageable.getPageNumber(), status, currency, customerId);
        
        Object cachedResult = cacheService.getCachedPaginatedResults(cacheKey);
        if (cachedResult != null) {
            return (Page<ChargeResponse>) cachedResult;
        }
        
        // Execute optimized query
        Page<Charge> charges;
        if (customerId != null) {
            charges = chargeRepository.findByCustomerIdAndStatusAndCurrency(
                customerId, status, currency, pageable);
        } else if (status != null && currency != null) {
            charges = chargeRepository.findByStatusAndCurrency(status, currency, pageable);
        } else if (status != null) {
            charges = chargeRepository.findByStatus(status, pageable);
        } else {
            charges = chargeRepository.findAll(pageable);
        }
        
        Page<ChargeResponse> response = charges.map(ChargeResponse::new);
        
        // Cache the result
        cacheService.cachePaginatedResults(cacheKey, response, java.time.Duration.ofMinutes(5));
        
        return response;
    }
    
    /**
     * Update charge status with cache invalidation
     */
    @CacheEvict(value = "charges", key = "#id")
    public ChargeResponse updateChargeStatus(Long id, String status) {
        Charge charge = chargeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Charge not found"));
        
        charge.setStatus(Charge.ChargeStatus.valueOf(status));
        charge.setUpdatedAt(LocalDateTime.now());
        
        Charge updatedCharge = chargeRepository.save(charge);
        
        // Publish webhook event asynchronously
        publishChargeEventAsync(updatedCharge);
        
        return new ChargeResponse(updatedCharge);
    }
    
    /**
     * Get charge statistics with caching
     */
    @Cacheable(value = "statistics", key = "'charge_stats'")
    public Object getChargeStatistics() {
        // Use materialized view for better performance
        return chargeRepository.getChargeStatistics();
    }
    
    /**
     * Get cached customer with fallback
     */
    private Customer getCachedCustomer(Long customerId) {
        // Try cache first
        Object cachedCustomer = cacheService.getCachedCustomer(customerId);
        if (cachedCustomer != null) {
            return (Customer) cachedCustomer;
        }
        
        // Fallback to database
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            cacheService.cacheCustomer(customerId, customer);
        }
        
        return customer;
    }
    
    /**
     * Process payment asynchronously
     */
    @Async("paymentExecutor")
    public CompletableFuture<Void> processPaymentAsync(Charge charge) {
        try {
            // Simulate payment processing
            Thread.sleep(50); // Simulate 50ms processing time
            
            // Update charge status
            charge.setStatus(Charge.ChargeStatus.SUCCEEDED);
            charge.setUpdatedAt(LocalDateTime.now());
            chargeRepository.save(charge);
            
            // Cache the updated charge
            cacheService.cacheCharge(charge.getId(), charge);
            
            // Publish webhook event
            webhookService.publishEvent("charge.succeeded", charge);
            
        } catch (Exception e) {
            // Handle payment failure
            charge.setStatus(Charge.ChargeStatus.FAILED);
            charge.setUpdatedAt(LocalDateTime.now());
            chargeRepository.save(charge);
            
            // Publish webhook event
            webhookService.publishEvent("charge.failed", charge);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Publish charge event asynchronously
     */
    @Async("webhookExecutor")
    public CompletableFuture<Void> publishChargeEventAsync(Charge charge) {
        String eventType = charge.getStatus() == Charge.ChargeStatus.SUCCEEDED ? 
            "charge.succeeded" : "charge.failed";
        
        webhookService.publishEvent(eventType, charge);
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Batch process charges for high throughput
     */
    @Async("paymentExecutor")
    public CompletableFuture<List<ChargeResponse>> processBatchCharges(List<CreateChargeRequest> requests) {
        List<ChargeResponse> responses = requests.parallelStream()
            .map(this::createCharge)
            .collect(Collectors.toList());
        
        return CompletableFuture.completedFuture(responses);
    }
    
    /**
     * Get recent charges with cache optimization
     */
    public List<ChargeResponse> getRecentCharges(int limit) {
        String cacheKey = "recent_charges:" + limit;
        Object cachedResult = cacheService.getCachedFrequentData(cacheKey);
        
        if (cachedResult != null) {
            return (List<ChargeResponse>) cachedResult;
        }
        
        List<Charge> charges = chargeRepository.findTop10ByOrderByCreatedAtDesc();
        List<ChargeResponse> response = charges.stream()
            .map(ChargeResponse::new)
            .collect(Collectors.toList());
        
        // Cache for 1 minute
        cacheService.cacheFrequentData(cacheKey, response, java.time.Duration.ofMinutes(1));
        
        return response;
    }
    
    /**
     * Search charges with full-text search
     */
    public List<ChargeResponse> searchCharges(String searchTerm, int limit) {
        String cacheKey = "search_charges:" + searchTerm + ":" + limit;
        Object cachedResult = cacheService.getCachedSearchResults(cacheKey);
        
        if (cachedResult != null) {
            return (List<ChargeResponse>) cachedResult;
        }
        
        // Use database function for optimized search
        List<Charge> charges = chargeRepository.searchCharges(searchTerm, limit);
        List<ChargeResponse> response = charges.stream()
            .map(ChargeResponse::new)
            .collect(Collectors.toList());
        
        // Cache search results for 5 minutes
        cacheService.cacheSearchResults(cacheKey, response, java.time.Duration.ofMinutes(5));
        
        return response;
    }
}


