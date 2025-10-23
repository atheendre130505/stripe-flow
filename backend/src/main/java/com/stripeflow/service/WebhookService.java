package com.stripeflow.service;

import com.stripeflow.model.WebhookEndpoint;
import com.stripeflow.model.WebhookEvent;
import com.stripeflow.repository.WebhookEndpointRepository;
import com.stripeflow.repository.WebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for webhook event publishing and delivery
 */
@Service
@Transactional
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    
    @Autowired
    private WebhookEndpointRepository webhookEndpointRepository;
    
    @Autowired
    private WebhookEventRepository webhookEventRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Publish a webhook event to all enabled endpoints
     */
    @Async
    public CompletableFuture<Void> publishEvent(String eventType, Object eventData) {
        logger.info("Publishing webhook event: {}", eventType);
        
        List<WebhookEndpoint> endpoints = webhookEndpointRepository.findByEnabledTrue();
        
        for (WebhookEndpoint endpoint : endpoints) {
            try {
                WebhookEvent webhookEvent = new WebhookEvent();
                webhookEvent.setEndpoint(endpoint);
                webhookEvent.setEventType(eventType);
                webhookEvent.setEventData(convertToJson(eventData));
                webhookEvent.setStatus(WebhookEvent.WebhookEventStatus.PENDING);
                webhookEvent.setNextRetry(LocalDateTime.now());
                
                WebhookEvent savedEvent = webhookEventRepository.save(webhookEvent);
                
                // Process webhook delivery asynchronously
                processWebhookDelivery(savedEvent);
                
            } catch (Exception e) {
                logger.error("Error publishing webhook event {} to endpoint {}: {}", 
                    eventType, endpoint.getUrl(), e.getMessage());
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Process webhook delivery with retry logic
     */
    @Async
    public CompletableFuture<Void> processWebhookDelivery(WebhookEvent webhookEvent) {
        logger.info("Processing webhook delivery for event {} to endpoint {}", 
            webhookEvent.getId(), webhookEvent.getEndpoint().getUrl());
        
        try {
            // Update attempt timestamp
            webhookEvent.setLastAttempt(LocalDateTime.now());
            webhookEvent.setStatus(WebhookEvent.WebhookEventStatus.RETRYING);
            webhookEventRepository.save(webhookEvent);
            
            // Make HTTP request to webhook endpoint
            String response = restTemplate.postForObject(
                webhookEvent.getEndpoint().getUrl(),
                createWebhookPayload(webhookEvent),
                String.class
            );
            
            // Mark as delivered
            webhookEvent.setStatus(WebhookEvent.WebhookEventStatus.DELIVERED);
            webhookEvent.setResponseCode(200);
            webhookEvent.setResponseBody(response);
            webhookEventRepository.save(webhookEvent);
            
            logger.info("Webhook event {} delivered successfully to {}", 
                webhookEvent.getId(), webhookEvent.getEndpoint().getUrl());
            
        } catch (Exception e) {
            logger.error("Webhook delivery failed for event {}: {}", 
                webhookEvent.getId(), e.getMessage());
            
            handleWebhookFailure(webhookEvent, e);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Handle webhook delivery failure with exponential backoff
     */
    private void handleWebhookFailure(WebhookEvent webhookEvent, Exception e) {
        webhookEvent.setRetryCount(webhookEvent.getRetryCount() + 1);
        
        if (webhookEvent.getRetryCount() >= webhookEvent.getMaxRetries()) {
            // Max retries exceeded, mark as failed
            webhookEvent.setStatus(WebhookEvent.WebhookEventStatus.FAILED);
            webhookEvent.setResponseBody(e.getMessage());
            logger.error("Webhook event {} failed after {} retries", 
                webhookEvent.getId(), webhookEvent.getRetryCount());
        } else {
            // Schedule retry with exponential backoff
            int delaySeconds = calculateRetryDelay(webhookEvent.getRetryCount());
            webhookEvent.setNextRetry(LocalDateTime.now().plusSeconds(delaySeconds));
            webhookEvent.setStatus(WebhookEvent.WebhookEventStatus.PENDING);
            logger.info("Webhook event {} scheduled for retry in {} seconds", 
                webhookEvent.getId(), delaySeconds);
        }
        
        webhookEventRepository.save(webhookEvent);
    }
    
    /**
     * Calculate retry delay with exponential backoff
     */
    private int calculateRetryDelay(int retryCount) {
        // Exponential backoff: 1s, 5s, 15s
        switch (retryCount) {
            case 1: return 1;
            case 2: return 5;
            case 3: return 15;
            default: return 60; // Max 1 minute
        }
    }
    
    /**
     * Create webhook payload with signature
     */
    private WebhookPayload createWebhookPayload(WebhookEvent webhookEvent) {
        WebhookPayload payload = new WebhookPayload();
        payload.setId(webhookEvent.getId().toString());
        payload.setType(webhookEvent.getEventType());
        payload.setData(webhookEvent.getEventData());
        payload.setCreated(LocalDateTime.now());
        
        // Add signature if secret is configured
        if (webhookEvent.getEndpoint().getSecret() != null) {
            String signature = generateSignature(payload, webhookEvent.getEndpoint().getSecret());
            payload.setSignature(signature);
        }
        
        return payload;
    }
    
    /**
     * Generate webhook signature for security
     */
    private String generateSignature(WebhookPayload payload, String secret) {
        // Simple HMAC-SHA256 signature (in production, use proper crypto library)
        String payloadString = payload.getId() + payload.getType() + payload.getData();
        return "sha256=" + payloadString.hashCode(); // Simplified for demo
    }
    
    /**
     * Convert object to JSON string
     */
    private String convertToJson(Object eventData) {
        // In production, use proper JSON serialization
        return eventData.toString();
    }
    
    /**
     * Webhook payload inner class
     */
    public static class WebhookPayload {
        private String id;
        private String type;
        private String data;
        private LocalDateTime created;
        private String signature;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public LocalDateTime getCreated() { return created; }
        public void setCreated(LocalDateTime created) { this.created = created; }
        
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
    }
}



