package com.stripeflow.service;

import com.stripeflow.dto.AddressDto;
import com.stripeflow.dto.CreateCustomerRequest;
import com.stripeflow.dto.CustomerResponse;
import com.stripeflow.model.Address;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private CustomerService customerService;
    
    private Customer testCustomer;
    private CreateCustomerRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setName("Test Customer");
        testCustomer.setPhone("1234567890");
        
        Address address = new Address();
        address.setLine1("123 Main St");
        address.setCity("Test City");
        address.setCountry("Test Country");
        testCustomer.setAddress(address);
        
        testRequest = new CreateCustomerRequest();
        testRequest.setEmail("test@example.com");
        testRequest.setName("Test Customer");
        testRequest.setPhone("1234567890");
        
        AddressDto addressDto = new AddressDto();
        addressDto.setLine1("123 Main St");
        addressDto.setCity("Test City");
        addressDto.setCountry("Test Country");
        testRequest.setAddress(addressDto);
    }
    
    @Test
    void createCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        
        // When
        CustomerResponse result = customerService.createCustomer(testRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        assertEquals(testCustomer.getName(), result.getName());
        verify(customerRepository).existsByEmail(testRequest.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void createCustomer_EmailAlreadyExists_ThrowsException() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.createCustomer(testRequest));
        
        assertEquals("Customer with email test@example.com already exists", exception.getMessage());
        verify(customerRepository).existsByEmail(testRequest.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void getCustomerById_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        // When
        CustomerResponse result = customerService.getCustomerById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        verify(customerRepository).findById(1L);
    }
    
    @Test
    void getCustomerById_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.getCustomerById(1L));
        
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).findById(1L);
    }
    
    @Test
    void getCustomerByEmail_Success() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));
        
        // When
        CustomerResponse result = customerService.getCustomerByEmail("test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        verify(customerRepository).findByEmail("test@example.com");
    }
    
    @Test
    void getCustomerByEmail_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.getCustomerByEmail("test@example.com"));
        
        assertEquals("Customer not found with email: test@example.com", exception.getMessage());
        verify(customerRepository).findByEmail("test@example.com");
    }
    
    @Test
    void getAllCustomers_Success() {
        // Given
        List<Customer> customers = Arrays.asList(testCustomer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        
        // When
        Page<CustomerResponse> result = customerService.getAllCustomers(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCustomer.getId(), result.getContent().get(0).getId());
        verify(customerRepository).findAll(pageable);
    }
    
    @Test
    void searchCustomersByName_Success() {
        // Given
        List<Customer> customers = Arrays.asList(testCustomer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(customerRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(customerPage);
        
        // When
        Page<CustomerResponse> result = customerService.searchCustomersByName("Test", pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCustomer.getId(), result.getContent().get(0).getId());
        verify(customerRepository).findByNameContainingIgnoreCase("Test", pageable);
    }
    
    @Test
    void updateCustomer_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        
        // When
        CustomerResponse result = customerService.updateCustomer(1L, testRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void updateCustomer_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.updateCustomer(1L, testRequest));
        
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void deleteCustomer_Success() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(true);
        
        // When
        customerService.deleteCustomer(1L);
        
        // Then
        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }
    
    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.deleteCustomer(1L));
        
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(any());
    }
    
    @Test
    void customerExists_ReturnsTrue() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(true);
        
        // When
        boolean result = customerService.customerExists(1L);
        
        // Then
        assertTrue(result);
        verify(customerRepository).existsById(1L);
    }
    
    @Test
    void customerExists_ReturnsFalse() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(false);
        
        // When
        boolean result = customerService.customerExists(1L);
        
        // Then
        assertFalse(result);
        verify(customerRepository).existsById(1L);
    }
}



