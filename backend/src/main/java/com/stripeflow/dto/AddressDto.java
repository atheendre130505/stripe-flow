package com.stripeflow.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for address information
 */
public class AddressDto {
    
    @Size(max = 255, message = "Line 1 must not exceed 255 characters")
    private String line1;
    
    @Size(max = 255, message = "Line 2 must not exceed 255 characters")
    private String line2;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    // Constructors
    public AddressDto() {}
    
    public AddressDto(String line1, String city, String country) {
        this.line1 = line1;
        this.city = city;
        this.country = country;
    }
    
    // Getters and Setters
    public String getLine1() {
        return line1;
    }
    
    public void setLine1(String line1) {
        this.line1 = line1;
    }
    
    public String getLine2() {
        return line2;
    }
    
    public void setLine2(String line2) {
        this.line2 = line2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
}



