package com.stripeflow.repository;

import com.stripeflow.model.Charge;
import com.stripeflow.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Charge entity operations
 */
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    
    /**
     * Find charges by customer
     */
    Page<Charge> findByCustomer(Customer customer, Pageable pageable);
    
    /**
     * Find charges by status
     */
    Page<Charge> findByStatus(Charge.ChargeStatus status, Pageable pageable);
    
    /**
     * Find charges by customer and status
     */
    Page<Charge> findByCustomerAndStatus(Customer customer, Charge.ChargeStatus status, Pageable pageable);
    
    /**
     * Find charge by idempotency key
     */
    Optional<Charge> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Check if idempotency key exists
     */
    boolean existsByIdempotencyKey(String idempotencyKey);
    
    /**
     * Find charges created within date range
     */
    @Query("SELECT c FROM Charge c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Charge> findChargesCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find charges by amount range
     */
    @Query("SELECT c FROM Charge c WHERE c.amount BETWEEN :minAmount AND :maxAmount")
    Page<Charge> findChargesByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                        @Param("maxAmount") BigDecimal maxAmount, 
                                        Pageable pageable);
    
    /**
     * Find charges by currency
     */
    Page<Charge> findByCurrency(String currency, Pageable pageable);
    
    /**
     * Count successful charges for a customer
     */
    @Query("SELECT COUNT(c) FROM Charge c WHERE c.customer = :customer AND c.status = 'SUCCEEDED'")
    long countSuccessfulChargesByCustomer(@Param("customer") Customer customer);
    
    /**
     * Calculate total amount of successful charges for a customer
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Charge c WHERE c.customer = :customer AND c.status = 'SUCCEEDED'")
    BigDecimal calculateTotalSuccessfulAmountByCustomer(@Param("customer") Customer customer);
    
    /**
     * Find charges by customer email
     */
    @Query("SELECT c FROM Charge c WHERE c.customer.email = :email")
    Page<Charge> findByCustomerEmail(@Param("email") String email, Pageable pageable);
    
    /**
     * Find recent charges for dashboard
     */
    @Query("SELECT c FROM Charge c ORDER BY c.createdAt DESC")
    Page<Charge> findRecentCharges(Pageable pageable);
    
    /**
     * Count charges by status
     */
    @Query("SELECT COUNT(c) FROM Charge c WHERE c.status = :status")
    long countChargesByStatus(@Param("status") Charge.ChargeStatus status);
    
    /**
     * Calculate total revenue by currency
     */
    @Query("SELECT c.currency, COALESCE(SUM(c.amount), 0) FROM Charge c WHERE c.status = 'SUCCEEDED' GROUP BY c.currency")
    List<Object[]> calculateRevenueByCurrency();
}

