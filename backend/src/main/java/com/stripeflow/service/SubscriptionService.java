package com.stripeflow.service;

import com.stripeflow.dto.CreateSubscriptionRequest;
import com.stripeflow.dto.SubscriptionResponse;
import com.stripeflow.model.Customer;
import com.stripeflow.model.Subscription;
import com.stripeflow.repository.CustomerRepository;
import com.stripeflow.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for subscription operations
 */
@Service
@Transactional
public class SubscriptionService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Create a new subscription
     */
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + request.getCustomerId()));
        
        // Validate period dates
        if (request.getCurrentPeriodStart().isAfter(request.getCurrentPeriodEnd())) {
            throw new IllegalArgumentException("Current period start must be before end date");
        }
        
        // Validate trial dates if provided
        if (request.getTrialStart() != null && request.getTrialEnd() != null) {
            if (request.getTrialStart().isAfter(request.getTrialEnd())) {
                throw new IllegalArgumentException("Trial start must be before trial end date");
            }
        }
        
        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setPlanId(request.getPlanId());
        subscription.setAmount(request.getAmount());
        subscription.setCurrency(request.getCurrency());
        subscription.setCurrentPeriodStart(request.getCurrentPeriodStart());
        subscription.setCurrentPeriodEnd(request.getCurrentPeriodEnd());
        subscription.setTrialStart(request.getTrialStart());
        subscription.setTrialEnd(request.getTrialEnd());
        subscription.setDescription(request.getDescription());
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return new SubscriptionResponse(savedSubscription);
    }
    
    /**
     * Get subscription by ID
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + id));
        return new SubscriptionResponse(subscription);
    }
    
    /**
     * Get all subscriptions with pagination
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable)
            .map(SubscriptionResponse::new);
    }
    
    /**
     * Get subscriptions by customer
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getSubscriptionsByCustomer(Long customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        
        return subscriptionRepository.findByCustomer(customer, pageable)
            .map(SubscriptionResponse::new);
    }
    
    /**
     * Get subscriptions by status
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getSubscriptionsByStatus(Subscription.SubscriptionStatus status, Pageable pageable) {
        return subscriptionRepository.findByStatus(status, pageable)
            .map(SubscriptionResponse::new);
    }
    
    /**
     * Get subscriptions by plan ID
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getSubscriptionsByPlanId(String planId, Pageable pageable) {
        return subscriptionRepository.findByPlanId(planId, pageable)
            .map(SubscriptionResponse::new);
    }
    
    /**
     * Get active subscriptions
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getActiveSubscriptions() {
        return subscriptionRepository.findActiveSubscriptions()
            .stream()
            .map(SubscriptionResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get subscriptions expiring soon
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsExpiringSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        
        return subscriptionRepository.findSubscriptionsExpiringSoon(now, futureDate)
            .stream()
            .map(SubscriptionResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent subscriptions
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getRecentSubscriptions(Pageable pageable) {
        return subscriptionRepository.findRecentSubscriptions(pageable)
            .map(SubscriptionResponse::new);
    }
    
    /**
     * Update subscription status
     */
    public SubscriptionResponse updateSubscriptionStatus(Long id, Subscription.SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + id));
        
        subscription.setStatus(status);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return new SubscriptionResponse(updatedSubscription);
    }
    
    /**
     * Cancel subscription
     */
    public SubscriptionResponse cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + id));
        
        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot cancel subscription with status: " + subscription.getStatus());
        }
        
        subscription.setStatus(Subscription.SubscriptionStatus.CANCELED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return new SubscriptionResponse(updatedSubscription);
    }
    
    /**
     * Get subscription statistics
     */
    @Transactional(readOnly = true)
    public SubscriptionStatistics getSubscriptionStatistics() {
        long totalSubscriptions = subscriptionRepository.count();
        long activeSubscriptions = subscriptionRepository.countActiveSubscriptions();
        long canceledSubscriptions = subscriptionRepository.countSubscriptionsByStatus(Subscription.SubscriptionStatus.CANCELED);
        
        return new SubscriptionStatistics(totalSubscriptions, activeSubscriptions, canceledSubscriptions);
    }
    
    /**
     * Get subscription entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Subscription getSubscriptionEntityById(Long id) {
        return subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + id));
    }
    
    /**
     * Subscription statistics inner class
     */
    public static class SubscriptionStatistics {
        private final long totalSubscriptions;
        private final long activeSubscriptions;
        private final long canceledSubscriptions;
        
        public SubscriptionStatistics(long totalSubscriptions, long activeSubscriptions, long canceledSubscriptions) {
            this.totalSubscriptions = totalSubscriptions;
            this.activeSubscriptions = activeSubscriptions;
            this.canceledSubscriptions = canceledSubscriptions;
        }
        
        public long getTotalSubscriptions() { return totalSubscriptions; }
        public long getActiveSubscriptions() { return activeSubscriptions; }
        public long getCanceledSubscriptions() { return canceledSubscriptions; }
        public double getActiveRate() { 
            return totalSubscriptions > 0 ? (double) activeSubscriptions / totalSubscriptions * 100 : 0; 
        }
    }
}



