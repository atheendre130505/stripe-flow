package com.stripeflow.repository;

import com.stripeflow.model.Customer;
import com.stripeflow.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Subscription entity operations
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    /**
     * Find subscriptions by customer
     */
    Page<Subscription> findByCustomer(Customer customer, Pageable pageable);
    
    /**
     * Find subscriptions by status
     */
    Page<Subscription> findByStatus(Subscription.SubscriptionStatus status, Pageable pageable);
    
    /**
     * Find subscriptions by customer and status
     */
    Page<Subscription> findByCustomerAndStatus(Customer customer, Subscription.SubscriptionStatus status, Pageable pageable);
    
    /**
     * Find subscriptions by plan ID
     */
    Page<Subscription> findByPlanId(String planId, Pageable pageable);
    
    /**
     * Find active subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE'")
    List<Subscription> findActiveSubscriptions();
    
    /**
     * Find subscriptions expiring soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.currentPeriodEnd BETWEEN :now AND :futureDate AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsExpiringSoon(@Param("now") LocalDateTime now, 
                                                   @Param("futureDate") LocalDateTime futureDate);
    
    /**
     * Find subscriptions created within date range
     */
    @Query("SELECT s FROM Subscription s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find subscriptions by customer email
     */
    @Query("SELECT s FROM Subscription s WHERE s.customer.email = :email")
    Page<Subscription> findByCustomerEmail(@Param("email") String email, Pageable pageable);
    
    /**
     * Count active subscriptions
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE'")
    long countActiveSubscriptions();
    
    /**
     * Count subscriptions by status
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
    long countSubscriptionsByStatus(@Param("status") Subscription.SubscriptionStatus status);
    
    /**
     * Find subscriptions by currency
     */
    Page<Subscription> findByCurrency(String currency, Pageable pageable);
    
    /**
     * Find recent subscriptions for dashboard
     */
    @Query("SELECT s FROM Subscription s ORDER BY s.createdAt DESC")
    Page<Subscription> findRecentSubscriptions(Pageable pageable);
    
    /**
     * Calculate total recurring revenue by currency
     */
    @Query("SELECT s.currency, COALESCE(SUM(s.amount), 0) FROM Subscription s WHERE s.status = 'ACTIVE' GROUP BY s.currency")
    List<Object[]> calculateRecurringRevenueByCurrency();
}



