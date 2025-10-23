package com.stripeflow.service;

import com.stripeflow.dto.CreateWebhookEndpointRequest;
import com.stripeflow.dto.WebhookEndpointResponse;
import com.stripeflow.dto.WebhookEventResponse;
import com.stripeflow.model.WebhookEndpoint;
import com.stripeflow.model.WebhookEvent;
import com.stripeflow.repository.WebhookEndpointRepository;
import com.stripeflow.repository.WebhookEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for webhook endpoint management
 */
@Service
@Transactional
public class WebhookManagementService {
    
    @Autowired
    private WebhookEndpointRepository webhookEndpointRepository;
    
    @Autowired
    private WebhookEventRepository webhookEventRepository;
    
    /**
     * Create a new webhook endpoint
     */
    public WebhookEndpointResponse createWebhookEndpoint(CreateWebhookEndpointRequest request) {
        // Check if endpoint already exists
        if (webhookEndpointRepository.existsByUrl(request.getUrl())) {
            throw new IllegalArgumentException("Webhook endpoint with URL " + request.getUrl() + " already exists");
        }
        
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUrl(request.getUrl());
        endpoint.setSecret(request.getSecret());
        endpoint.setEnabled(request.getEnabled());
        endpoint.setDescription(request.getDescription());
        
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(endpoint);
        return new WebhookEndpointResponse(savedEndpoint);
    }
    
    /**
     * Get webhook endpoint by ID
     */
    @Transactional(readOnly = true)
    public WebhookEndpointResponse getWebhookEndpointById(Long id) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Webhook endpoint not found with ID: " + id));
        return new WebhookEndpointResponse(endpoint);
    }
    
    /**
     * Get all webhook endpoints with pagination
     */
    @Transactional(readOnly = true)
    public Page<WebhookEndpointResponse> getAllWebhookEndpoints(Pageable pageable) {
        return webhookEndpointRepository.findAll(pageable)
            .map(WebhookEndpointResponse::new);
    }
    
    /**
     * Get webhook endpoints by enabled status
     */
    @Transactional(readOnly = true)
    public Page<WebhookEndpointResponse> getWebhookEndpointsByEnabled(Boolean enabled, Pageable pageable) {
        return webhookEndpointRepository.findByEnabled(enabled, pageable)
            .map(WebhookEndpointResponse::new);
    }
    
    /**
     * Update webhook endpoint
     */
    public WebhookEndpointResponse updateWebhookEndpoint(Long id, CreateWebhookEndpointRequest request) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Webhook endpoint not found with ID: " + id));
        
        // Check if URL is being changed and if it already exists
        if (!endpoint.getUrl().equals(request.getUrl()) && 
            webhookEndpointRepository.existsByUrl(request.getUrl())) {
            throw new IllegalArgumentException("Webhook endpoint with URL " + request.getUrl() + " already exists");
        }
        
        endpoint.setUrl(request.getUrl());
        endpoint.setSecret(request.getSecret());
        endpoint.setEnabled(request.getEnabled());
        endpoint.setDescription(request.getDescription());
        
        WebhookEndpoint updatedEndpoint = webhookEndpointRepository.save(endpoint);
        return new WebhookEndpointResponse(updatedEndpoint);
    }
    
    /**
     * Delete webhook endpoint
     */
    public void deleteWebhookEndpoint(Long id) {
        if (!webhookEndpointRepository.existsById(id)) {
            throw new IllegalArgumentException("Webhook endpoint not found with ID: " + id);
        }
        webhookEndpointRepository.deleteById(id);
    }
    
    /**
     * Enable/disable webhook endpoint
     */
    public WebhookEndpointResponse toggleWebhookEndpoint(Long id, Boolean enabled) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Webhook endpoint not found with ID: " + id));
        
        endpoint.setEnabled(enabled);
        WebhookEndpoint updatedEndpoint = webhookEndpointRepository.save(endpoint);
        return new WebhookEndpointResponse(updatedEndpoint);
    }
    
    /**
     * Get webhook events by endpoint
     */
    @Transactional(readOnly = true)
    public Page<WebhookEventResponse> getWebhookEventsByEndpoint(Long endpointId, Pageable pageable) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(endpointId)
            .orElseThrow(() -> new IllegalArgumentException("Webhook endpoint not found with ID: " + endpointId));
        
        return webhookEventRepository.findByEndpoint(endpoint, pageable)
            .map(WebhookEventResponse::new);
    }
    
    /**
     * Get webhook events by status
     */
    @Transactional(readOnly = true)
    public Page<WebhookEventResponse> getWebhookEventsByStatus(WebhookEvent.WebhookEventStatus status, Pageable pageable) {
        return webhookEventRepository.findByStatus(status, pageable)
            .map(WebhookEventResponse::new);
    }
    
    /**
     * Get recent webhook events
     */
    @Transactional(readOnly = true)
    public Page<WebhookEventResponse> getRecentWebhookEvents(Pageable pageable) {
        return webhookEventRepository.findRecentEvents(pageable)
            .map(WebhookEventResponse::new);
    }
    
    /**
     * Get failed webhook events
     */
    @Transactional(readOnly = true)
    public Page<WebhookEventResponse> getFailedWebhookEvents(Pageable pageable) {
        return webhookEventRepository.findFailedEvents(pageable)
            .map(WebhookEventResponse::new);
    }
    
    /**
     * Retry failed webhook event
     */
    public WebhookEventResponse retryWebhookEvent(Long eventId) {
        WebhookEvent event = webhookEventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Webhook event not found with ID: " + eventId));
        
        if (event.getStatus() != WebhookEvent.WebhookEventStatus.FAILED) {
            throw new IllegalStateException("Cannot retry webhook event with status: " + event.getStatus());
        }
        
        event.setStatus(WebhookEvent.WebhookEventStatus.PENDING);
        event.setRetryCount(0);
        event.setNextRetry(java.time.LocalDateTime.now());
        
        WebhookEvent updatedEvent = webhookEventRepository.save(event);
        return new WebhookEventResponse(updatedEvent);
    }
    
    /**
     * Get webhook statistics
     */
    @Transactional(readOnly = true)
    public WebhookStatistics getWebhookStatistics() {
        long totalEndpoints = webhookEndpointRepository.count();
        long enabledEndpoints = webhookEndpointRepository.countEnabledEndpoints();
        long totalEvents = webhookEventRepository.count();
        long pendingEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.PENDING);
        long deliveredEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.DELIVERED);
        long failedEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.FAILED);
        
        return new WebhookStatistics(totalEndpoints, enabledEndpoints, totalEvents, 
                                    pendingEvents, deliveredEvents, failedEvents);
    }
    
    /**
     * Webhook statistics inner class
     */
    public static class WebhookStatistics {
        private final long totalEndpoints;
        private final long enabledEndpoints;
        private final long totalEvents;
        private final long pendingEvents;
        private final long deliveredEvents;
        private final long failedEvents;
        
        public WebhookStatistics(long totalEndpoints, long enabledEndpoints, long totalEvents,
                               long pendingEvents, long deliveredEvents, long failedEvents) {
            this.totalEndpoints = totalEndpoints;
            this.enabledEndpoints = enabledEndpoints;
            this.totalEvents = totalEvents;
            this.pendingEvents = pendingEvents;
            this.deliveredEvents = deliveredEvents;
            this.failedEvents = failedEvents;
        }
        
        public long getTotalEndpoints() { return totalEndpoints; }
        public long getEnabledEndpoints() { return enabledEndpoints; }
        public long getTotalEvents() { return totalEvents; }
        public long getPendingEvents() { return pendingEvents; }
        public long getDeliveredEvents() { return deliveredEvents; }
        public long getFailedEvents() { return failedEvents; }
        public double getDeliverySuccessRate() { 
            return totalEvents > 0 ? (double) deliveredEvents / totalEvents * 100 : 0; 
        }
    }
}



