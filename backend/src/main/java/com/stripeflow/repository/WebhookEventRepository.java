package com.stripeflow.repository;

import com.stripeflow.model.WebhookEvent;
import com.stripeflow.model.WebhookEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for WebhookEvent entity operations
 */
@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    
    /**
     * Find webhook events by endpoint
     */
    Page<WebhookEvent> findByEndpoint(WebhookEndpoint endpoint, Pageable pageable);
    
    /**
     * Find webhook events by status
     */
    Page<WebhookEvent> findByStatus(WebhookEvent.WebhookEventStatus status, Pageable pageable);
    
    /**
     * Find webhook events by endpoint and status
     */
    Page<WebhookEvent> findByEndpointAndStatus(WebhookEndpoint endpoint, 
                                             WebhookEvent.WebhookEventStatus status, 
                                             Pageable pageable);
    
    /**
     * Find webhook events by event type
     */
    Page<WebhookEvent> findByEventType(String eventType, Pageable pageable);
    
    /**
     * Find webhook events ready for retry
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.status = 'PENDING' AND w.nextRetry <= :now")
    List<WebhookEvent> findEventsReadyForRetry(@Param("now") LocalDateTime now);
    
    /**
     * Find webhook events by endpoint and event type
     */
    Page<WebhookEvent> findByEndpointAndEventType(WebhookEndpoint endpoint, 
                                                  String eventType, 
                                                  Pageable pageable);
    
    /**
     * Find webhook events created within date range
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.createdAt BETWEEN :startDate AND :endDate")
    List<WebhookEvent> findEventsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count webhook events by status
     */
    @Query("SELECT COUNT(w) FROM WebhookEvent w WHERE w.status = :status")
    long countEventsByStatus(@Param("status") WebhookEvent.WebhookEventStatus status);
    
    /**
     * Count webhook events by endpoint
     */
    @Query("SELECT COUNT(w) FROM WebhookEvent w WHERE w.endpoint = :endpoint")
    long countEventsByEndpoint(@Param("endpoint") WebhookEndpoint endpoint);
    
    /**
     * Find recent webhook events
     */
    @Query("SELECT w FROM WebhookEvent w ORDER BY w.createdAt DESC")
    Page<WebhookEvent> findRecentEvents(Pageable pageable);
    
    /**
     * Find webhook events by endpoint with pagination
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.endpoint = :endpoint ORDER BY w.createdAt DESC")
    Page<WebhookEvent> findEventsByEndpointOrderByCreatedAtDesc(WebhookEndpoint endpoint, Pageable pageable);
    
    /**
     * Find failed webhook events
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.status = 'FAILED' ORDER BY w.createdAt DESC")
    Page<WebhookEvent> findFailedEvents(Pageable pageable);
    
    /**
     * Find webhook events by response code
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.responseCode = :responseCode")
    Page<WebhookEvent> findEventsByResponseCode(@Param("responseCode") Integer responseCode, Pageable pageable);
    
    /**
     * Calculate webhook delivery success rate
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN w.status = 'DELIVERED' THEN 1 END) * 100.0 / COUNT(*) " +
           "FROM WebhookEvent w WHERE w.endpoint = :endpoint")
    Double calculateSuccessRateByEndpoint(@Param("endpoint") WebhookEndpoint endpoint);
    
    /**
     * Find webhook events for cleanup (older than specified days)
     */
    @Query("SELECT w FROM WebhookEvent w WHERE w.createdAt < :cutoffDate")
    List<WebhookEvent> findEventsForCleanup(@Param("cutoffDate") LocalDateTime cutoffDate);
}

