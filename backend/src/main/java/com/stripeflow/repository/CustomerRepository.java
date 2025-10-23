package com.stripeflow.repository;

import com.stripeflow.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by email
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Check if customer exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find customers by name containing (case-insensitive)
     */
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find customers created within date range
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Customer> findCustomersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count customers created in the last N days
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :since")
    long countCustomersCreatedSince(@Param("since") LocalDateTime since);
    
    /**
     * Find customers with active charges
     */
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.charges ch WHERE ch.status = 'SUCCEEDED'")
    List<Customer> findCustomersWithSuccessfulCharges();
    
    /**
     * Find customers by partial email match
     */
    @Query("SELECT c FROM Customer c WHERE c.email LIKE %:emailPattern%")
    List<Customer> findByEmailContaining(@Param("emailPattern") String emailPattern);
}



