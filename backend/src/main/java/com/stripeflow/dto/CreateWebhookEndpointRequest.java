package com.stripeflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for creating a new webhook endpoint
 */
public class CreateWebhookEndpointRequest {
    
    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;
    
    @Size(max = 255, message = "Secret must not exceed 255 characters")
    private String secret;
    
    private Boolean enabled = true;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    // Constructors
    public CreateWebhookEndpointRequest() {}
    
    public CreateWebhookEndpointRequest(String url) {
        this.url = url;
    }
    
    // Getters and Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
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
}

