package com.stripeflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripeflow.dto.AddressDto;
import com.stripeflow.dto.CreateCustomerRequest;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CustomerController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        customerRepository.deleteAll();
    }
    
    @Test
    void createCustomer_Success() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("test@example.com");
        request.setName("Test Customer");
        request.setPhone("1234567890");
        
        AddressDto address = new AddressDto();
        address.setLine1("123 Main St");
        address.setCity("Test City");
        address.setCountry("Test Country");
        request.setAddress(address);
        
        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.address.line1").value("123 Main St"))
                .andExpect(jsonPath("$.address.city").value("Test City"))
                .andExpect(jsonPath("$.address.country").value("Test Country"));
    }
    
    @Test
    void createCustomer_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("invalid-email");
        request.setName("Test Customer");
        
        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createCustomer_MissingName_ReturnsBadRequest() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("test@example.com");
        // Missing name
        
        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createCustomer_DuplicateEmail_ReturnsBadRequest() throws Exception {
        // Given
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("test@example.com");
        existingCustomer.setName("Existing Customer");
        customerRepository.save(existingCustomer);
        
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("test@example.com");
        request.setName("New Customer");
        
        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getCustomer_Success() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setName("Test Customer");
        customer.setPhone("1234567890");
        Customer savedCustomer = customerRepository.save(customer);
        
        // When & Then
        mockMvc.perform(get("/api/v1/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }
    
    @Test
    void getCustomer_NotFound_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getAllCustomers_Success() throws Exception {
        // Given
        Customer customer1 = new Customer();
        customer1.setEmail("test1@example.com");
        customer1.setName("Test Customer 1");
        customerRepository.save(customer1);
        
        Customer customer2 = new Customer();
        customer2.setEmail("test2@example.com");
        customer2.setName("Test Customer 2");
        customerRepository.save(customer2);
        
        // When & Then
        mockMvc.perform(get("/api/v1/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
    
    @Test
    void searchCustomersByName_Success() throws Exception {
        // Given
        Customer customer1 = new Customer();
        customer1.setEmail("test1@example.com");
        customer1.setName("John Doe");
        customerRepository.save(customer1);
        
        Customer customer2 = new Customer();
        customer2.setEmail("test2@example.com");
        customer2.setName("Jane Smith");
        customerRepository.save(customer2);
        
        // When & Then
        mockMvc.perform(get("/api/v1/customers/search")
                .param("name", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }
    
    @Test
    void updateCustomer_Success() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setName("Test Customer");
        Customer savedCustomer = customerRepository.save(customer);
        
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("updated@example.com");
        request.setName("Updated Customer");
        request.setPhone("9876543210");
        
        // When & Then
        mockMvc.perform(put("/api/v1/customers/{id}", savedCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated Customer"))
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }
    
    @Test
    void updateCustomer_NotFound_ReturnsNotFound() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("updated@example.com");
        request.setName("Updated Customer");
        
        // When & Then
        mockMvc.perform(put("/api/v1/customers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteCustomer_Success() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setName("Test Customer");
        Customer savedCustomer = customerRepository.save(customer);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isNoContent());
        
        // Verify customer is deleted
        assertFalse(customerRepository.existsById(savedCustomer.getId()));
    }
    
    @Test
    void deleteCustomer_NotFound_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}

