package com.stripeflow.controller;

import com.stripeflow.dto.CreateSubscriptionRequest;
import com.stripeflow.dto.SubscriptionResponse;
import com.stripeflow.model.Subscription;
import com.stripeflow.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for subscription operations
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription operations")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    /**
     * Create a new subscription
     */
    @PostMapping
    @Operation(summary = "Create subscription", description = "Create a new subscription")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subscription created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        try {
            SubscriptionResponse subscription = subscriptionService.createSubscription(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get subscription by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get subscription", description = "Get subscription by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription found"),
        @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResponse> getSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long id) {
        try {
            SubscriptionResponse subscription = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all subscriptions with pagination
     */
    @GetMapping
    @Operation(summary = "Get all subscriptions", description = "Get all subscriptions with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<Page<SubscriptionResponse>> getAllSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions(
            org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Get subscriptions by customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get subscriptions by customer", description = "Get all subscriptions for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Page<SubscriptionResponse>> getSubscriptionsByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByCustomer(
                customerId, org.springframework.data.domain.PageRequest.of(page, size));
            return ResponseEntity.ok(subscriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get subscriptions by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get subscriptions by status", description = "Get subscriptions by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<Page<SubscriptionResponse>> getSubscriptionsByStatus(
            @Parameter(description = "Subscription status") @PathVariable Subscription.SubscriptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByStatus(
            status, org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Get subscriptions by plan ID
     */
    @GetMapping("/plan/{planId}")
    @Operation(summary = "Get subscriptions by plan", description = "Get subscriptions by plan ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<Page<SubscriptionResponse>> getSubscriptionsByPlanId(
            @Parameter(description = "Plan ID") @PathVariable String planId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByPlanId(
            planId, org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Get active subscriptions
     */
    @GetMapping("/active")
    @Operation(summary = "Get active subscriptions", description = "Get all active subscriptions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<List<SubscriptionResponse>> getActiveSubscriptions() {
        List<SubscriptionResponse> subscriptions = subscriptionService.getActiveSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Get subscriptions expiring soon
     */
    @GetMapping("/expiring")
    @Operation(summary = "Get expiring subscriptions", description = "Get subscriptions expiring within specified days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsExpiringSoon(
            @Parameter(description = "Days ahead to check") @RequestParam(defaultValue = "7") int days) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsExpiringSoon(days);
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Get recent subscriptions
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent subscriptions", description = "Get recent subscriptions for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    })
    public ResponseEntity<Page<SubscriptionResponse>> getRecentSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.getRecentSubscriptions(
            org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Update subscription status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update subscription status", description = "Update subscription status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResponse> updateSubscriptionStatus(
            @Parameter(description = "Subscription ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam Subscription.SubscriptionStatus status) {
        try {
            SubscriptionResponse subscription = subscriptionService.updateSubscriptionStatus(id, status);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cancel subscription
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel an active subscription")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription canceled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel subscription"),
        @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long id) {
        try {
            SubscriptionResponse subscription = subscriptionService.cancelSubscription(id);
            return ResponseEntity.ok(subscription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get subscription statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get subscription statistics", description = "Get subscription statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<SubscriptionService.SubscriptionStatistics> getSubscriptionStatistics() {
        SubscriptionService.SubscriptionStatistics statistics = subscriptionService.getSubscriptionStatistics();
        return ResponseEntity.ok(statistics);
    }
}

