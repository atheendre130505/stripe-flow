package com.stripeflow.service;

import com.stripeflow.dto.CreateCustomerRequest;
import com.stripeflow.dto.CustomerResponse;
import com.stripeflow.model.Address;
import com.stripeflow.model.Customer;
import com.stripeflow.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for customer operations
 */
@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Create a new customer
     */
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        // Check if customer already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + request.getEmail() + " already exists");
        }
        
        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        
        // Set address if provided
        if (request.getAddress() != null) {
            Address address = new Address();
            address.setLine1(request.getAddress().getLine1());
            address.setLine2(request.getAddress().getLine2());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setPostalCode(request.getAddress().getPostalCode());
            address.setCountry(request.getAddress().getCountry());
            customer.setAddress(address);
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponse(savedCustomer);
    }
    
    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        return new CustomerResponse(customer);
    }
    
    /**
     * Get customer by email
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));
        return new CustomerResponse(customer);
    }
    
    /**
     * Get all customers with pagination
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
            .map(CustomerResponse::new);
    }
    
    /**
     * Search customers by name
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomersByName(String name, Pageable pageable) {
        return customerRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(CustomerResponse::new);
    }
    
    /**
     * Update customer
     */
    public CustomerResponse updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        // Check if email is being changed and if it already exists
        if (!customer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + request.getEmail() + " already exists");
        }
        
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        
        // Update address if provided
        if (request.getAddress() != null) {
            if (customer.getAddress() == null) {
                customer.setAddress(new Address());
            }
            customer.getAddress().setLine1(request.getAddress().getLine1());
            customer.getAddress().setLine2(request.getAddress().getLine2());
            customer.getAddress().setCity(request.getAddress().getCity());
            customer.getAddress().setState(request.getAddress().getState());
            customer.getAddress().setPostalCode(request.getAddress().getPostalCode());
            customer.getAddress().setCountry(request.getAddress().getCountry());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        return new CustomerResponse(updatedCustomer);
    }
    
    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    /**
     * Get customers with successful charges
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithSuccessfulCharges() {
        return customerRepository.findCustomersWithSuccessfulCharges()
            .stream()
            .map(CustomerResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if customer exists
     */
    @Transactional(readOnly = true)
    public boolean customerExists(Long id) {
        return customerRepository.existsById(id);
    }
    
    /**
     * Get customer entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Customer getCustomerEntityById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
    }
}



