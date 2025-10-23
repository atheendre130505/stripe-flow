package com.stripeflow.repository;

import com.stripeflow.model.Charge;
import com.stripeflow.model.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Refund entity operations
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    /**
     * Find refunds by charge
     */
    Page<Refund> findByCharge(Charge charge, Pageable pageable);
    
    /**
     * Find refunds by status
     */
    Page<Refund> findByStatus(Refund.RefundStatus status, Pageable pageable);
    
    /**
     * Find refunds by charge and status
     */
    Page<Refund> findByChargeAndStatus(Charge charge, Refund.RefundStatus status, Pageable pageable);
    
    /**
     * Find refunds created within date range
     */
    @Query("SELECT r FROM Refund r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Refund> findRefundsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculate total refunded amount for a charge
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.charge = :charge AND r.status = 'SUCCEEDED'")
    BigDecimal calculateTotalRefundedAmountByCharge(@Param("charge") Charge charge);
    
    /**
     * Count successful refunds for a charge
     */
    @Query("SELECT COUNT(r) FROM Refund r WHERE r.charge = :charge AND r.status = 'SUCCEEDED'")
    long countSuccessfulRefundsByCharge(@Param("charge") Charge charge);
    
    /**
     * Find refunds by customer email
     */
    @Query("SELECT r FROM Refund r WHERE r.charge.customer.email = :email")
    Page<Refund> findByCustomerEmail(@Param("email") String email, Pageable pageable);
    
    /**
     * Find recent refunds for dashboard
     */
    @Query("SELECT r FROM Refund r ORDER BY r.createdAt DESC")
    Page<Refund> findRecentRefunds(Pageable pageable);
    
    /**
     * Count refunds by status
     */
    @Query("SELECT COUNT(r) FROM Refund r WHERE r.status = :status")
    long countRefundsByStatus(@Param("status") Refund.RefundStatus status);
    
    /**
     * Calculate total refunded amount by currency
     */
    @Query("SELECT r.charge.currency, COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.status = 'SUCCEEDED' GROUP BY r.charge.currency")
    List<Object[]> calculateRefundedAmountByCurrency();
}



