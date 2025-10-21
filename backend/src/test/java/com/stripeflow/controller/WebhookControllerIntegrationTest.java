package com.stripeflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripeflow.dto.CreateWebhookEndpointRequest;
import com.stripeflow.model.WebhookEndpoint;
import com.stripeflow.repository.WebhookEndpointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WebhookController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class WebhookControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private WebhookEndpointRepository webhookEndpointRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        webhookEndpointRepository.deleteAll();
    }
    
    @Test
    void createWebhookEndpoint_Success() throws Exception {
        // Given
        CreateWebhookEndpointRequest request = new CreateWebhookEndpointRequest();
        request.setUrl("https://example.com/webhook");
        request.setSecret("test-secret");
        request.setEnabled(true);
        request.setDescription("Test webhook");
        
        // When & Then
        mockMvc.perform(post("/api/v1/webhooks/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").value("https://example.com/webhook"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.description").value("Test webhook"));
    }
    
    @Test
    void createWebhookEndpoint_InvalidUrl() throws Exception {
        // Given
        CreateWebhookEndpointRequest request = new CreateWebhookEndpointRequest();
        request.setUrl("invalid-url");
        request.setEnabled(true);
        
        // When & Then
        mockMvc.perform(post("/api/v1/webhooks/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createWebhookEndpoint_MissingUrl() throws Exception {
        // Given
        CreateWebhookEndpointRequest request = new CreateWebhookEndpointRequest();
        request.setEnabled(true);
        
        // When & Then
        mockMvc.perform(post("/api/v1/webhooks/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getWebhookEndpoint_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUrl("https://example.com/webhook");
        endpoint.setSecret("test-secret");
        endpoint.setEnabled(true);
        endpoint.setDescription("Test webhook");
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(endpoint);
        
        // When & Then
        mockMvc.perform(get("/api/v1/webhooks/endpoints/{id}", savedEndpoint.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEndpoint.getId()))
                .andExpect(jsonPath("$.url").value("https://example.com/webhook"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.description").value("Test webhook"));
    }
    
    @Test
    void getWebhookEndpoint_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/webhooks/endpoints/{id}", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getAllWebhookEndpoints_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint1 = new WebhookEndpoint();
        endpoint1.setUrl("https://example1.com/webhook");
        endpoint1.setEnabled(true);
        webhookEndpointRepository.save(endpoint1);
        
        WebhookEndpoint endpoint2 = new WebhookEndpoint();
        endpoint2.setUrl("https://example2.com/webhook");
        endpoint2.setEnabled(false);
        webhookEndpointRepository.save(endpoint2);
        
        // When & Then
        mockMvc.perform(get("/api/v1/webhooks/endpoints")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
    
    @Test
    void updateWebhookEndpoint_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUrl("https://example.com/webhook");
        endpoint.setEnabled(true);
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(endpoint);
        
        CreateWebhookEndpointRequest request = new CreateWebhookEndpointRequest();
        request.setUrl("https://example.com/webhook-updated");
        request.setEnabled(false);
        request.setDescription("Updated webhook");
        
        // When & Then
        mockMvc.perform(put("/api/v1/webhooks/endpoints/{id}", savedEndpoint.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://example.com/webhook-updated"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.description").value("Updated webhook"));
    }
    
    @Test
    void updateWebhookEndpoint_NotFound() throws Exception {
        // Given
        CreateWebhookEndpointRequest request = new CreateWebhookEndpointRequest();
        request.setUrl("https://example.com/webhook");
        request.setEnabled(true);
        
        // When & Then
        mockMvc.perform(put("/api/v1/webhooks/endpoints/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteWebhookEndpoint_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUrl("https://example.com/webhook");
        endpoint.setEnabled(true);
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(endpoint);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/webhooks/endpoints/{id}", savedEndpoint.getId()))
                .andExpect(status().isNoContent());
        
        // Verify deletion
        assertFalse(webhookEndpointRepository.existsById(savedEndpoint.getId()));
    }
    
    @Test
    void deleteWebhookEndpoint_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/webhooks/endpoints/{id}", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void toggleWebhookEndpoint_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUrl("https://example.com/webhook");
        endpoint.setEnabled(true);
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(endpoint);
        
        // When & Then
        mockMvc.perform(put("/api/v1/webhooks/endpoints/{id}/toggle?enabled=false", savedEndpoint.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }
    
    @Test
    void toggleWebhookEndpoint_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/webhooks/endpoints/{id}/toggle?enabled=false", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getWebhookStatistics_Success() throws Exception {
        // Given
        WebhookEndpoint endpoint1 = new WebhookEndpoint();
        endpoint1.setUrl("https://example1.com/webhook");
        endpoint1.setEnabled(true);
        webhookEndpointRepository.save(endpoint1);
        
        WebhookEndpoint endpoint2 = new WebhookEndpoint();
        endpoint2.setUrl("https://example2.com/webhook");
        endpoint2.setEnabled(false);
        webhookEndpointRepository.save(endpoint2);
        
        // When & Then
        mockMvc.perform(get("/api/v1/webhooks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEndpoints").value(2))
                .andExpect(jsonPath("$.enabledEndpoints").value(1));
    }
}