package com.stripeflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Webhook event entity for tracking webhook deliveries
 */
@Entity
@Table(name = "webhook_events")
public class WebhookEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Endpoint is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private WebhookEndpoint endpoint;
    
    @NotBlank(message = "Event type is required")
    @Size(max = 100, message = "Event type must not exceed 100 characters")
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @NotBlank(message = "Event data is required")
    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventStatus status = WebhookEventStatus.PENDING;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;
    
    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;
    
    @Column(name = "next_retry")
    private LocalDateTime nextRetry;
    
    @Column(name = "response_code")
    private Integer responseCode;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Constructors
    public WebhookEvent() {}
    
    public WebhookEvent(WebhookEndpoint endpoint, String eventType, String eventData) {
        this.endpoint = endpoint;
        this.eventType = eventType;
        this.eventData = eventData;
    }
    
    // Enums
    public enum WebhookEventStatus {
        PENDING, DELIVERED, FAILED, RETRYING, CANCELED
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public WebhookEndpoint getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(WebhookEndpoint endpoint) {
        this.endpoint = endpoint;
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
    
    public WebhookEventStatus getStatus() {
        return status;
    }
    
    public void setStatus(WebhookEventStatus status) {
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

