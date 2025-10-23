package com.stripeflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new customer
 */
public class CreateCustomerRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;
    
    @Valid
    private AddressDto address;
    
    // Constructors
    public CreateCustomerRequest() {}
    
    public CreateCustomerRequest(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    // Getters and Setters
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
}



