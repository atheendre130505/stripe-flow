package com.stripeflow.controller;

import com.stripeflow.dto.CreateRefundRequest;
import com.stripeflow.dto.RefundResponse;
import com.stripeflow.model.Refund;
import com.stripeflow.service.RefundService;
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
 * REST controller for refund operations
 */
@RestController
@RequestMapping("/api/v1/refunds")
@Tag(name = "Refunds", description = "Refund operations")
public class RefundController {
    
    @Autowired
    private RefundService refundService;
    
    /**
     * Create a new refund
     */
    @PostMapping
    @Operation(summary = "Create refund", description = "Create a new refund for a charge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refund created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Charge not found"),
        @ApiResponse(responseCode = "409", description = "Cannot refund charge")
    })
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody CreateRefundRequest request) {
        try {
            RefundResponse refund = refundService.createRefund(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(refund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    /**
     * Get refund by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get refund", description = "Get refund by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund found"),
        @ApiResponse(responseCode = "404", description = "Refund not found")
    })
    public ResponseEntity<RefundResponse> getRefund(
            @Parameter(description = "Refund ID") @PathVariable Long id) {
        try {
            RefundResponse refund = refundService.getRefundById(id);
            return ResponseEntity.ok(refund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all refunds with pagination
     */
    @GetMapping
    @Operation(summary = "Get all refunds", description = "Get all refunds with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully")
    })
    public ResponseEntity<Page<RefundResponse>> getAllRefunds(Pageable pageable) {
        Page<RefundResponse> refunds = refundService.getAllRefunds(pageable);
        return ResponseEntity.ok(refunds);
    }
    
    /**
     * Get refunds by charge
     */
    @GetMapping("/charge/{chargeId}")
    @Operation(summary = "Get refunds by charge", description = "Get all refunds for a specific charge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Charge not found")
    })
    public ResponseEntity<Page<RefundResponse>> getRefundsByCharge(
            @Parameter(description = "Charge ID") @PathVariable Long chargeId,
            Pageable pageable) {
        try {
            Page<RefundResponse> refunds = refundService.getRefundsByCharge(chargeId, pageable);
            return ResponseEntity.ok(refunds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get refunds by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get refunds by status", description = "Get refunds by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully")
    })
    public ResponseEntity<Page<RefundResponse>> getRefundsByStatus(
            @Parameter(description = "Refund status") @PathVariable Refund.RefundStatus status,
            Pageable pageable) {
        Page<RefundResponse> refunds = refundService.getRefundsByStatus(status, pageable);
        return ResponseEntity.ok(refunds);
    }
    
    /**
     * Get recent refunds
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent refunds", description = "Get recent refunds for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully")
    })
    public ResponseEntity<Page<RefundResponse>> getRecentRefunds(Pageable pageable) {
        Page<RefundResponse> refunds = refundService.getRecentRefunds(pageable);
        return ResponseEntity.ok(refunds);
    }
    
    /**
     * Update refund status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update refund status", description = "Update refund status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found")
    })
    public ResponseEntity<RefundResponse> updateRefundStatus(
            @Parameter(description = "Refund ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam Refund.RefundStatus status) {
        try {
            RefundResponse refund = refundService.updateRefundStatus(id, status);
            return ResponseEntity.ok(refund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Cancel refund
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel refund", description = "Cancel a pending refund")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund canceled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel refund"),
        @ApiResponse(responseCode = "404", description = "Refund not found")
    })
    public ResponseEntity<RefundResponse> cancelRefund(
            @Parameter(description = "Refund ID") @PathVariable Long id) {
        try {
            RefundResponse refund = refundService.cancelRefund(id);
            return ResponseEntity.ok(refund);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get refund statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get refund statistics", description = "Get refund statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<RefundService.RefundStatistics> getRefundStatistics() {
        RefundService.RefundStatistics statistics = refundService.getRefundStatistics();
        return ResponseEntity.ok(statistics);
    }
}



