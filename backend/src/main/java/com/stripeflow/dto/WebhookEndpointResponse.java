package com.stripeflow.dto;

import com.stripeflow.model.WebhookEndpoint;

import java.time.LocalDateTime;

/**
 * DTO for webhook endpoint response
 */
public class WebhookEndpointResponse {
    
    private Long id;
    private String url;
    private Boolean enabled;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public WebhookEndpointResponse() {}
    
    public WebhookEndpointResponse(WebhookEndpoint endpoint) {
        this.id = endpoint.getId();
        this.url = endpoint.getUrl();
        this.enabled = endpoint.getEnabled();
        this.description = endpoint.getDescription();
        this.createdAt = endpoint.getCreatedAt();
        this.updatedAt = endpoint.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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



