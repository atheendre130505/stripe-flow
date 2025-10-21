package com.stripeflow.controller;

import com.stripeflow.dto.ChargeResponse;
import com.stripeflow.dto.CreateChargeRequest;
import com.stripeflow.model.Charge;
import com.stripeflow.service.ChargeService;
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
 * REST controller for charge operations
 */
@RestController
@RequestMapping("/api/v1/charges")
@Tag(name = "Charges", description = "Payment charge operations")
public class ChargeController {
    
    @Autowired
    private ChargeService chargeService;
    
    /**
     * Create a new charge
     */
    @PostMapping
    @Operation(summary = "Create charge", description = "Create a new payment charge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Charge created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<ChargeResponse> createCharge(@Valid @RequestBody CreateChargeRequest request) {
        try {
            ChargeResponse charge = chargeService.createCharge(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(charge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get charge by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get charge", description = "Get charge by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charge found"),
        @ApiResponse(responseCode = "404", description = "Charge not found")
    })
    public ResponseEntity<ChargeResponse> getCharge(
            @Parameter(description = "Charge ID") @PathVariable Long id) {
        try {
            ChargeResponse charge = chargeService.getChargeById(id);
            return ResponseEntity.ok(charge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all charges with pagination
     */
    @GetMapping
    @Operation(summary = "Get all charges", description = "Get all charges with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charges retrieved successfully")
    })
    public ResponseEntity<Page<ChargeResponse>> getAllCharges(Pageable pageable) {
        Page<ChargeResponse> charges = chargeService.getAllCharges(pageable);
        return ResponseEntity.ok(charges);
    }
    
    /**
     * Get charges by customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get charges by customer", description = "Get all charges for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charges retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Page<ChargeResponse>> getChargesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            Pageable pageable) {
        try {
            Page<ChargeResponse> charges = chargeService.getChargesByCustomer(customerId, pageable);
            return ResponseEntity.ok(charges);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get charges by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get charges by status", description = "Get charges by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charges retrieved successfully")
    })
    public ResponseEntity<Page<ChargeResponse>> getChargesByStatus(
            @Parameter(description = "Charge status") @PathVariable Charge.ChargeStatus status,
            Pageable pageable) {
        Page<ChargeResponse> charges = chargeService.getChargesByStatus(status, pageable);
        return ResponseEntity.ok(charges);
    }
    
    /**
     * Get charges by currency
     */
    @GetMapping("/currency/{currency}")
    @Operation(summary = "Get charges by currency", description = "Get charges by currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charges retrieved successfully")
    })
    public ResponseEntity<Page<ChargeResponse>> getChargesByCurrency(
            @Parameter(description = "Currency code") @PathVariable String currency,
            Pageable pageable) {
        Page<ChargeResponse> charges = chargeService.getChargesByCurrency(currency, pageable);
        return ResponseEntity.ok(charges);
    }
    
    /**
     * Get recent charges
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent charges", description = "Get recent charges for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charges retrieved successfully")
    })
    public ResponseEntity<Page<ChargeResponse>> getRecentCharges(Pageable pageable) {
        Page<ChargeResponse> charges = chargeService.getRecentCharges(pageable);
        return ResponseEntity.ok(charges);
    }
    
    /**
     * Update charge status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update charge status", description = "Update charge status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charge status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Charge not found")
    })
    public ResponseEntity<ChargeResponse> updateChargeStatus(
            @Parameter(description = "Charge ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam Charge.ChargeStatus status) {
        try {
            ChargeResponse charge = chargeService.updateChargeStatus(id, status);
            return ResponseEntity.ok(charge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cancel charge
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel charge", description = "Cancel a pending or processing charge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Charge canceled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel charge"),
        @ApiResponse(responseCode = "404", description = "Charge not found")
    })
    public ResponseEntity<ChargeResponse> cancelCharge(
            @Parameter(description = "Charge ID") @PathVariable Long id) {
        try {
            ChargeResponse charge = chargeService.cancelCharge(id);
            return ResponseEntity.ok(charge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get charge statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get charge statistics", description = "Get charge statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<ChargeService.ChargeStatistics> getChargeStatistics() {
        ChargeService.ChargeStatistics statistics = chargeService.getChargeStatistics();
        return ResponseEntity.ok(statistics);
    }
}

