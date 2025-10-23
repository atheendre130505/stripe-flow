package com.stripeflow.service;

import com.stripeflow.model.WebhookEndpoint;
import com.stripeflow.model.WebhookEvent;
import com.stripeflow.repository.WebhookEndpointRepository;
import com.stripeflow.repository.WebhookEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebhookService
 */
@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {
    
    @Mock
    private WebhookEndpointRepository webhookEndpointRepository;
    
    @Mock
    private WebhookEventRepository webhookEventRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private WebhookService webhookService;
    
    private WebhookEndpoint testEndpoint;
    
    @BeforeEach
    void setUp() {
        testEndpoint = new WebhookEndpoint();
        testEndpoint.setId(1L);
        testEndpoint.setUrl("https://example.com/webhook");
        testEndpoint.setSecret("test-secret");
        testEndpoint.setEnabled(true);
    }
    
    @Test
    void publishEvent_Success() {
        // Given
        when(webhookEndpointRepository.findByEnabledTrue()).thenReturn(Arrays.asList(testEndpoint));
        when(webhookEventRepository.save(any(WebhookEvent.class))).thenReturn(new WebhookEvent());
        
        // When
        CompletableFuture<Void> result = webhookService.publishEvent("charge.succeeded", "test data");
        
        // Then
        assertNotNull(result);
        verify(webhookEndpointRepository).findByEnabledTrue();
        verify(webhookEventRepository).save(any(WebhookEvent.class));
    }
    
    @Test
    void publishEvent_NoEndpoints() {
        // Given
        when(webhookEndpointRepository.findByEnabledTrue()).thenReturn(Arrays.asList());
        
        // When
        CompletableFuture<Void> result = webhookService.publishEvent("charge.succeeded", "test data");
        
        // Then
        assertNotNull(result);
        verify(webhookEndpointRepository).findByEnabledTrue();
        verify(webhookEventRepository, never()).save(any(WebhookEvent.class));
    }
    
    @Test
    void processWebhookDelivery_Success() {
        // Given
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setEndpoint(testEndpoint);
        webhookEvent.setEventType("charge.succeeded");
        webhookEvent.setEventData("test data");
        
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn("success");
        when(webhookEventRepository.save(any(WebhookEvent.class))).thenReturn(webhookEvent);
        
        // When
        CompletableFuture<Void> result = webhookService.processWebhookDelivery(webhookEvent);
        
        // Then
        assertNotNull(result);
        verify(restTemplate).postForObject(anyString(), any(), any());
        verify(webhookEventRepository, atLeastOnce()).save(any(WebhookEvent.class));
    }
    
    @Test
    void processWebhookDelivery_Failure() {
        // Given
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setEndpoint(testEndpoint);
        webhookEvent.setEventType("charge.succeeded");
        webhookEvent.setEventData("test data");
        webhookEvent.setRetryCount(0);
        webhookEvent.setMaxRetries(3);
        
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenThrow(new RuntimeException("Connection failed"));
        when(webhookEventRepository.save(any(WebhookEvent.class))).thenReturn(webhookEvent);
        
        // When
        CompletableFuture<Void> result = webhookService.processWebhookDelivery(webhookEvent);
        
        // Then
        assertNotNull(result);
        verify(restTemplate).postForObject(anyString(), any(), any());
        verify(webhookEventRepository, atLeastOnce()).save(any(WebhookEvent.class));
    }
    
    @Test
    void processWebhookDelivery_MaxRetriesExceeded() {
        // Given
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setEndpoint(testEndpoint);
        webhookEvent.setEventType("charge.succeeded");
        webhookEvent.setEventData("test data");
        webhookEvent.setRetryCount(3);
        webhookEvent.setMaxRetries(3);
        
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenThrow(new RuntimeException("Connection failed"));
        when(webhookEventRepository.save(any(WebhookEvent.class))).thenReturn(webhookEvent);
        
        // When
        CompletableFuture<Void> result = webhookService.processWebhookDelivery(webhookEvent);
        
        // Then
        assertNotNull(result);
        verify(restTemplate).postForObject(anyString(), any(), any());
        verify(webhookEventRepository, atLeastOnce()).save(any(WebhookEvent.class));
    }
}



