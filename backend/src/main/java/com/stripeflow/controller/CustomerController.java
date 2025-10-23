package com.stripeflow.controller;

import com.stripeflow.dto.CreateCustomerRequest;
import com.stripeflow.dto.CustomerResponse;
import com.stripeflow.service.CustomerService;
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
 * REST controller for customer operations
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management operations")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * Create a new customer
     */
    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Customer already exists")
    })
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        try {
            CustomerResponse customer = customerService.createCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get customer", description = "Get customer by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponse> getCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        try {
            CustomerResponse customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all customers with pagination
     */
    @GetMapping
    @Operation(summary = "Get all customers", description = "Get all customers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        Page<CustomerResponse> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Search customers by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers found")
    })
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @Parameter(description = "Name to search") @RequestParam String name,
            Pageable pageable) {
        Page<CustomerResponse> customers = customerService.searchCustomersByName(name, pageable);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Update customer
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update customer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Valid @RequestBody CreateCustomerRequest request) {
        try {
            CustomerResponse customer = customerService.updateCustomer(id, request);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete customer by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get customers with successful charges
     */
    @GetMapping("/successful-charges")
    @Operation(summary = "Get customers with successful charges", description = "Get customers who have successful charges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public ResponseEntity<Page<CustomerResponse>> getCustomersWithSuccessfulCharges(Pageable pageable) {
        // This would need to be implemented in the service layer with pagination
        // For now, return empty page
        return ResponseEntity.ok(Page.empty());
    }
}



