package com.stripeflow.repository;

import com.stripeflow.model.WebhookEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for WebhookEndpoint entity operations
 */
@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    
    /**
     * Find enabled webhook endpoints
     */
    List<WebhookEndpoint> findByEnabledTrue();
    
    /**
     * Find webhook endpoints by URL
     */
    List<WebhookEndpoint> findByUrl(String url);
    
    /**
     * Check if webhook endpoint exists by URL
     */
    boolean existsByUrl(String url);
    
    /**
     * Find webhook endpoints with pagination
     */
    Page<WebhookEndpoint> findAll(Pageable pageable);
    
    /**
     * Find webhook endpoints by enabled status
     */
    Page<WebhookEndpoint> findByEnabled(Boolean enabled, Pageable pageable);
    
    /**
     * Find webhook endpoints by URL containing
     */
    @Query("SELECT w FROM WebhookEndpoint w WHERE w.url LIKE %:urlPattern%")
    List<WebhookEndpoint> findByUrlContaining(@Param("urlPattern") String urlPattern);
    
    /**
     * Count enabled webhook endpoints
     */
    @Query("SELECT COUNT(w) FROM WebhookEndpoint w WHERE w.enabled = true")
    long countEnabledEndpoints();
    
    /**
     * Find webhook endpoints created within date range
     */
    @Query("SELECT w FROM WebhookEndpoint w WHERE w.createdAt BETWEEN :startDate AND :endDate")
    List<WebhookEndpoint> findEndpointsCreatedBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                                     @Param("endDate") java.time.LocalDateTime endDate);
}

