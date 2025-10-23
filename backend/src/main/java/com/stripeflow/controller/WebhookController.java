package com.stripeflow.controller;

import com.stripeflow.dto.CreateWebhookEndpointRequest;
import com.stripeflow.dto.WebhookEndpointResponse;
import com.stripeflow.dto.WebhookEventResponse;
import com.stripeflow.model.WebhookEvent;
import com.stripeflow.service.WebhookManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for webhook management operations
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks", description = "Webhook management operations")
public class WebhookController {
    
    @Autowired
    private WebhookManagementService webhookManagementService;
    
    /**
     * Create a new webhook endpoint
     */
    @PostMapping("/endpoints")
    @Operation(summary = "Create webhook endpoint", description = "Create a new webhook endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Webhook endpoint created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Webhook endpoint already exists")
    })
    public ResponseEntity<WebhookEndpointResponse> createWebhookEndpoint(@Valid @RequestBody CreateWebhookEndpointRequest request) {
        try {
            WebhookEndpointResponse endpoint = webhookManagementService.createWebhookEndpoint(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(endpoint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get webhook endpoint by ID
     */
    @GetMapping("/endpoints/{id}")
    @Operation(summary = "Get webhook endpoint", description = "Get webhook endpoint by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook endpoint found"),
        @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    })
    public ResponseEntity<WebhookEndpointResponse> getWebhookEndpoint(
            @Parameter(description = "Webhook endpoint ID") @PathVariable Long id) {
        try {
            WebhookEndpointResponse endpoint = webhookManagementService.getWebhookEndpointById(id);
            return ResponseEntity.ok(endpoint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all webhook endpoints
     */
    @GetMapping("/endpoints")
    @Operation(summary = "Get all webhook endpoints", description = "Get all webhook endpoints with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook endpoints retrieved successfully")
    })
    public ResponseEntity<Page<WebhookEndpointResponse>> getAllWebhookEndpoints(Pageable pageable) {
        Page<WebhookEndpointResponse> endpoints = webhookManagementService.getAllWebhookEndpoints(pageable);
        return ResponseEntity.ok(endpoints);
    }
    
    /**
     * Update webhook endpoint
     */
    @PutMapping("/endpoints/{id}")
    @Operation(summary = "Update webhook endpoint", description = "Update webhook endpoint information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook endpoint updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Webhook endpoint not found"),
        @ApiResponse(responseCode = "409", description = "URL already exists")
    })
    public ResponseEntity<WebhookEndpointResponse> updateWebhookEndpoint(
            @Parameter(description = "Webhook endpoint ID") @PathVariable Long id,
            @Valid @RequestBody CreateWebhookEndpointRequest request) {
        try {
            WebhookEndpointResponse endpoint = webhookManagementService.updateWebhookEndpoint(id, request);
            return ResponseEntity.ok(endpoint);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete webhook endpoint
     */
    @DeleteMapping("/endpoints/{id}")
    @Operation(summary = "Delete webhook endpoint", description = "Delete webhook endpoint by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Webhook endpoint deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    })
    public ResponseEntity<Void> deleteWebhookEndpoint(
            @Parameter(description = "Webhook endpoint ID") @PathVariable Long id) {
        try {
            webhookManagementService.deleteWebhookEndpoint(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Toggle webhook endpoint enabled status
     */
    @PutMapping("/endpoints/{id}/toggle")
    @Operation(summary = "Toggle webhook endpoint", description = "Enable or disable webhook endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook endpoint status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    })
    public ResponseEntity<WebhookEndpointResponse> toggleWebhookEndpoint(
            @Parameter(description = "Webhook endpoint ID") @PathVariable Long id,
            @Parameter(description = "Enabled status") @RequestParam Boolean enabled) {
        try {
            WebhookEndpointResponse endpoint = webhookManagementService.toggleWebhookEndpoint(id, enabled);
            return ResponseEntity.ok(endpoint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get webhook events by endpoint
     */
    @GetMapping("/endpoints/{id}/events")
    @Operation(summary = "Get webhook events by endpoint", description = "Get webhook events for a specific endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Webhook endpoint not found")
    })
    public ResponseEntity<Page<WebhookEventResponse>> getWebhookEventsByEndpoint(
            @Parameter(description = "Webhook endpoint ID") @PathVariable Long id,
            Pageable pageable) {
        try {
            Page<WebhookEventResponse> events = webhookManagementService.getWebhookEventsByEndpoint(id, pageable);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get webhook events by status
     */
    @GetMapping("/events/status/{status}")
    @Operation(summary = "Get webhook events by status", description = "Get webhook events by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook events retrieved successfully")
    })
    public ResponseEntity<Page<WebhookEventResponse>> getWebhookEventsByStatus(
            @Parameter(description = "Event status") @PathVariable WebhookEvent.WebhookEventStatus status,
            Pageable pageable) {
        Page<WebhookEventResponse> events = webhookManagementService.getWebhookEventsByStatus(status, pageable);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get recent webhook events
     */
    @GetMapping("/events/recent")
    @Operation(summary = "Get recent webhook events", description = "Get recent webhook events for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook events retrieved successfully")
    })
    public ResponseEntity<Page<WebhookEventResponse>> getRecentWebhookEvents(Pageable pageable) {
        Page<WebhookEventResponse> events = webhookManagementService.getRecentWebhookEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get failed webhook events
     */
    @GetMapping("/events/failed")
    @Operation(summary = "Get failed webhook events", description = "Get failed webhook events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook events retrieved successfully")
    })
    public ResponseEntity<Page<WebhookEventResponse>> getFailedWebhookEvents(Pageable pageable) {
        Page<WebhookEventResponse> events = webhookManagementService.getFailedWebhookEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Retry failed webhook event
     */
    @PutMapping("/events/{id}/retry")
    @Operation(summary = "Retry webhook event", description = "Retry a failed webhook event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook event retry initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot retry webhook event"),
        @ApiResponse(responseCode = "404", description = "Webhook event not found")
    })
    public ResponseEntity<WebhookEventResponse> retryWebhookEvent(
            @Parameter(description = "Webhook event ID") @PathVariable Long id) {
        try {
            WebhookEventResponse event = webhookManagementService.retryWebhookEvent(id);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get webhook statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get webhook statistics", description = "Get webhook delivery statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<WebhookManagementService.WebhookStatistics> getWebhookStatistics() {
        WebhookManagementService.WebhookStatistics statistics = webhookManagementService.getWebhookStatistics();
        return ResponseEntity.ok(statistics);
    }
}



