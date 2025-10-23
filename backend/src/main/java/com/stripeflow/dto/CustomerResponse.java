package com.stripeflow.dto;

import com.stripeflow.model.Customer;

import java.time.LocalDateTime;

/**
 * DTO for customer response
 */
public class CustomerResponse {
    
    private Long id;
    private String email;
    private String name;
    private String phone;
    private AddressDto address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CustomerResponse() {}
    
    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.name = customer.getName();
        this.phone = customer.getPhone();
        this.createdAt = customer.getCreatedAt();
        this.updatedAt = customer.getUpdatedAt();
        
        if (customer.getAddress() != null) {
            this.address = new AddressDto();
            this.address.setLine1(customer.getAddress().getLine1());
            this.address.setLine2(customer.getAddress().getLine2());
            this.address.setCity(customer.getAddress().getCity());
            this.address.setState(customer.getAddress().getState());
            this.address.setPostalCode(customer.getAddress().getPostalCode());
            this.address.setCountry(customer.getAddress().getCountry());
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public AddressDto getAddress() {
        return address;
    }
    
    public void setAddress(AddressDto address) {
        this.address = address;
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



