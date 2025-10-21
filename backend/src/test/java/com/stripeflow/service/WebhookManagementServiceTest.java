package com.stripeflow.service;

import com.stripeflow.dto.CreateWebhookEndpointRequest;
import com.stripeflow.dto.WebhookEndpointResponse;
import com.stripeflow.dto.WebhookEventResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebhookManagementService
 */
@ExtendWith(MockitoExtension.class)
class WebhookManagementServiceTest {
    
    @Mock
    private WebhookEndpointRepository webhookEndpointRepository;
    
    @Mock
    private WebhookEventRepository webhookEventRepository;
    
    @InjectMocks
    private WebhookManagementService webhookManagementService;
    
    private WebhookEndpoint testEndpoint;
    private CreateWebhookEndpointRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testEndpoint = new WebhookEndpoint();
        testEndpoint.setId(1L);
        testEndpoint.setUrl("https://example.com/webhook");
        testEndpoint.setSecret("test-secret");
        testEndpoint.setEnabled(true);
        testEndpoint.setDescription("Test webhook");
        testEndpoint.setCreatedAt(LocalDateTime.now());
        testEndpoint.setUpdatedAt(LocalDateTime.now());
        
        testRequest = new CreateWebhookEndpointRequest();
        testRequest.setUrl("https://example.com/webhook");
        testRequest.setSecret("test-secret");
        testRequest.setEnabled(true);
        testRequest.setDescription("Test webhook");
    }
    
    @Test
    void createWebhookEndpoint_Success() {
        // Given
        when(webhookEndpointRepository.existsByUrl(testRequest.getUrl())).thenReturn(false);
        when(webhookEndpointRepository.save(any(WebhookEndpoint.class))).thenReturn(testEndpoint);
        
        // When
        WebhookEndpointResponse response = webhookManagementService.createWebhookEndpoint(testRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(testEndpoint.getId(), response.getId());
        assertEquals(testEndpoint.getUrl(), response.getUrl());
        assertEquals(testEndpoint.getEnabled(), response.getEnabled());
        assertEquals(testEndpoint.getDescription(), response.getDescription());
        
        verify(webhookEndpointRepository).existsByUrl(testRequest.getUrl());
        verify(webhookEndpointRepository).save(any(WebhookEndpoint.class));
    }
    
    @Test
    void createWebhookEndpoint_UrlAlreadyExists() {
        // Given
        when(webhookEndpointRepository.existsByUrl(testRequest.getUrl())).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> webhookManagementService.createWebhookEndpoint(testRequest)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(webhookEndpointRepository).existsByUrl(testRequest.getUrl());
        verify(webhookEndpointRepository, never()).save(any(WebhookEndpoint.class));
    }
    
    @Test
    void getWebhookEndpointById_Success() {
        // Given
        when(webhookEndpointRepository.findById(1L)).thenReturn(Optional.of(testEndpoint));
        
        // When
        WebhookEndpointResponse response = webhookManagementService.getWebhookEndpointById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(testEndpoint.getId(), response.getId());
        assertEquals(testEndpoint.getUrl(), response.getUrl());
        verify(webhookEndpointRepository).findById(1L);
    }
    
    @Test
    void getWebhookEndpointById_NotFound() {
        // Given
        when(webhookEndpointRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> webhookManagementService.getWebhookEndpointById(1L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(webhookEndpointRepository).findById(1L);
    }
    
    @Test
    void getAllWebhookEndpoints_Success() {
        // Given
        Page<WebhookEndpoint> page = new PageImpl<>(Arrays.asList(testEndpoint));
        when(webhookEndpointRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<WebhookEndpointResponse> response = webhookManagementService.getAllWebhookEndpoints(
            org.springframework.data.domain.PageRequest.of(0, 10)
        );
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(testEndpoint.getId(), response.getContent().get(0).getId());
        verify(webhookEndpointRepository).findAll(any(Pageable.class));
    }
    
    @Test
    void updateWebhookEndpoint_Success() {
        // Given
        when(webhookEndpointRepository.findById(1L)).thenReturn(Optional.of(testEndpoint));
        when(webhookEndpointRepository.existsByUrl(testRequest.getUrl())).thenReturn(false);
        when(webhookEndpointRepository.save(any(WebhookEndpoint.class))).thenReturn(testEndpoint);
        
        // When
        WebhookEndpointResponse response = webhookManagementService.updateWebhookEndpoint(1L, testRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(testEndpoint.getId(), response.getId());
        verify(webhookEndpointRepository).findById(1L);
        verify(webhookEndpointRepository).existsByUrl(testRequest.getUrl());
        verify(webhookEndpointRepository).save(any(WebhookEndpoint.class));
    }
    
    @Test
    void updateWebhookEndpoint_NotFound() {
        // Given
        when(webhookEndpointRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> webhookManagementService.updateWebhookEndpoint(1L, testRequest)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(webhookEndpointRepository).findById(1L);
        verify(webhookEndpointRepository, never()).save(any(WebhookEndpoint.class));
    }
    
    @Test
    void deleteWebhookEndpoint_Success() {
        // Given
        when(webhookEndpointRepository.existsById(1L)).thenReturn(true);
        
        // When
        webhookManagementService.deleteWebhookEndpoint(1L);
        
        // Then
        verify(webhookEndpointRepository).existsById(1L);
        verify(webhookEndpointRepository).deleteById(1L);
    }
    
    @Test
    void deleteWebhookEndpoint_NotFound() {
        // Given
        when(webhookEndpointRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> webhookManagementService.deleteWebhookEndpoint(1L)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(webhookEndpointRepository).existsById(1L);
        verify(webhookEndpointRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void toggleWebhookEndpoint_Success() {
        // Given
        when(webhookEndpointRepository.findById(1L)).thenReturn(Optional.of(testEndpoint));
        when(webhookEndpointRepository.save(any(WebhookEndpoint.class))).thenReturn(testEndpoint);
        
        // When
        WebhookEndpointResponse response = webhookManagementService.toggleWebhookEndpoint(1L, false);
        
        // Then
        assertNotNull(response);
        assertFalse(testEndpoint.getEnabled());
        verify(webhookEndpointRepository).findById(1L);
        verify(webhookEndpointRepository).save(any(WebhookEndpoint.class));
    }
    
    @Test
    void getWebhookStatistics_Success() {
        // Given
        when(webhookEndpointRepository.count()).thenReturn(5L);
        when(webhookEndpointRepository.countEnabledEndpoints()).thenReturn(3L);
        when(webhookEventRepository.count()).thenReturn(100L);
        when(webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.PENDING)).thenReturn(10L);
        when(webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.DELIVERED)).thenReturn(80L);
        when(webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.FAILED)).thenReturn(10L);
        
        // When
        WebhookManagementService.WebhookStatistics statistics = webhookManagementService.getWebhookStatistics();
        
        // Then
        assertNotNull(statistics);
        assertEquals(5L, statistics.getTotalEndpoints());
        assertEquals(3L, statistics.getEnabledEndpoints());
        assertEquals(100L, statistics.getTotalEvents());
        assertEquals(10L, statistics.getPendingEvents());
        assertEquals(80L, statistics.getDeliveredEvents());
        assertEquals(10L, statistics.getFailedEvents());
        assertEquals(80.0, statistics.getDeliverySuccessRate());
        
        verify(webhookEndpointRepository).count();
        verify(webhookEndpointRepository).countEnabledEndpoints();
        verify(webhookEventRepository).count();
        verify(webhookEventRepository).countEventsByStatus(WebhookEvent.WebhookEventStatus.PENDING);
        verify(webhookEventRepository).countEventsByStatus(WebhookEvent.WebhookEventStatus.DELIVERED);
        verify(webhookEventRepository).countEventsByStatus(WebhookEvent.WebhookEventStatus.FAILED);
    }
}
