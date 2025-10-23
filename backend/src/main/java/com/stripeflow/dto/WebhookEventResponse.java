package com.stripeflow.dto;

import com.stripeflow.model.WebhookEvent;

import java.time.LocalDateTime;

/**
 * DTO for webhook event response
 */
public class WebhookEventResponse {
    
    private Long id;
    private Long endpointId;
    private String eventType;
    private String eventData;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime lastAttempt;
    private LocalDateTime nextRetry;
    private Integer responseCode;
    private String responseBody;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public WebhookEventResponse() {}
    
    public WebhookEventResponse(WebhookEvent event) {
        this.id = event.getId();
        this.endpointId = event.getEndpoint().getId();
        this.eventType = event.getEventType();
        this.eventData = event.getEventData();
        this.status = event.getStatus().name();
        this.retryCount = event.getRetryCount();
        this.maxRetries = event.getMaxRetries();
        this.lastAttempt = event.getLastAttempt();
        this.nextRetry = event.getNextRetry();
        this.responseCode = event.getResponseCode();
        this.responseBody = event.getResponseBody();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEndpointId() {
        return endpointId;
    }
    
    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }
    
    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }
    
    public LocalDateTime getNextRetry() {
        return nextRetry;
    }
    
    public void setNextRetry(LocalDateTime nextRetry) {
        this.nextRetry = nextRetry;
    }
    
    public Integer getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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



