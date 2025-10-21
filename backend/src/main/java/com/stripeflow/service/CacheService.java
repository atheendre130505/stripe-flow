package com.stripeflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * High-performance caching service for StripeFlow
 */
@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Cache keys constants
    private static final String CUSTOMER_PREFIX = "customer:";
    private static final String CHARGE_PREFIX = "charge:";
    private static final String REFUND_PREFIX = "refund:";
    private static final String SUBSCRIPTION_PREFIX = "subscription:";
    private static final String STATISTICS_PREFIX = "statistics:";
    private static final String SESSION_PREFIX = "session:";
    private static final String API_KEY_PREFIX = "apikey:";
    
    /**
     * Cache customer data with TTL
     */
    @CachePut(value = "customers", key = "#customerId")
    public Object cacheCustomer(Long customerId, Object customer) {
        return customer;
    }
    
    /**
     * Get cached customer data
     */
    @Cacheable(value = "customers", key = "#customerId")
    public Object getCachedCustomer(Long customerId) {
        return null; // Will be populated by cache
    }
    
    /**
     * Evict customer cache
     */
    @CacheEvict(value = "customers", key = "#customerId")
    public void evictCustomer(Long customerId) {
        // Cache eviction handled by annotation
    }
    
    /**
     * Cache charge data with TTL
     */
    @CachePut(value = "charges", key = "#chargeId")
    public Object cacheCharge(Long chargeId, Object charge) {
        return charge;
    }
    
    /**
     * Get cached charge data
     */
    @Cacheable(value = "charges", key = "#chargeId")
    public Object getCachedCharge(Long chargeId) {
        return null; // Will be populated by cache
    }
    
    /**
     * Evict charge cache
     */
    @CacheEvict(value = "charges", key = "#chargeId")
    public void evictCharge(Long chargeId) {
        // Cache eviction handled by annotation
    }
    
    /**
     * Cache statistics with short TTL
     */
    @CachePut(value = "statistics", key = "#statisticsType")
    public Object cacheStatistics(String statisticsType, Object statistics) {
        return statistics;
    }
    
    /**
     * Get cached statistics
     */
    @Cacheable(value = "statistics", key = "#statisticsType")
    public Object getCachedStatistics(String statisticsType) {
        return null; // Will be populated by cache
    }
    
    /**
     * Cache API key validation
     */
    public void cacheApiKeyValidation(String apiKey, boolean isValid) {
        String key = API_KEY_PREFIX + apiKey;
        redisTemplate.opsForValue().set(key, isValid, Duration.ofHours(1));
    }
    
    /**
     * Get cached API key validation
     */
    public Boolean getCachedApiKeyValidation(String apiKey) {
        String key = API_KEY_PREFIX + apiKey;
        return (Boolean) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Cache session data
     */
    public void cacheSession(String sessionId, Object sessionData) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, sessionData, Duration.ofHours(24));
    }
    
    /**
     * Get cached session data
     */
    public Object getCachedSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Evict session cache
     */
    public void evictSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
    
    /**
     * Cache paginated results
     */
    public void cachePaginatedResults(String cacheKey, Object results, Duration ttl) {
        redisTemplate.opsForValue().set(cacheKey, results, ttl);
    }
    
    /**
     * Get cached paginated results
     */
    public Object getCachedPaginatedResults(String cacheKey) {
        return redisTemplate.opsForValue().get(cacheKey);
    }
    
    /**
     * Cache search results
     */
    public void cacheSearchResults(String searchKey, Object results, Duration ttl) {
        redisTemplate.opsForValue().set("search:" + searchKey, results, ttl);
    }
    
    /**
     * Get cached search results
     */
    public Object getCachedSearchResults(String searchKey) {
        return redisTemplate.opsForValue().get("search:" + searchKey);
    }
    
    /**
     * Cache rate limiting data
     */
    public void cacheRateLimit(String key, int count, Duration window) {
        redisTemplate.opsForValue().set("rate_limit:" + key, count, window);
    }
    
    /**
     * Get cached rate limit data
     */
    public Integer getCachedRateLimit(String key) {
        return (Integer) redisTemplate.opsForValue().get("rate_limit:" + key);
    }
    
    /**
     * Increment rate limit counter
     */
    public Long incrementRateLimit(String key, Duration window) {
        String rateLimitKey = "rate_limit:" + key;
        Long count = redisTemplate.opsForValue().increment(rateLimitKey);
        if (count == 1) {
            redisTemplate.expire(rateLimitKey, window);
        }
        return count;
    }
    
    /**
     * Cache webhook delivery status
     */
    public void cacheWebhookStatus(Long webhookId, String status) {
        String key = "webhook_status:" + webhookId;
        redisTemplate.opsForValue().set(key, status, Duration.ofMinutes(30));
    }
    
    /**
     * Get cached webhook status
     */
    public String getCachedWebhookStatus(Long webhookId) {
        String key = "webhook_status:" + webhookId;
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Cache payment processing status
     */
    public void cachePaymentStatus(String paymentId, String status) {
        String key = "payment_status:" + paymentId;
        redisTemplate.opsForValue().set(key, status, Duration.ofHours(1));
    }
    
    /**
     * Get cached payment status
     */
    public String getCachedPaymentStatus(String paymentId) {
        String key = "payment_status:" + paymentId;
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Cache frequently accessed data
     */
    public void cacheFrequentData(String key, Object data, Duration ttl) {
        redisTemplate.opsForValue().set("frequent:" + key, data, ttl);
    }
    
    /**
     * Get cached frequent data
     */
    public Object getCachedFrequentData(String key) {
        return redisTemplate.opsForValue().get("frequent:" + key);
    }
    
    /**
     * Cache with sliding expiration
     */
    public void cacheWithSlidingExpiration(String key, Object data, Duration ttl) {
        redisTemplate.opsForValue().set("sliding:" + key, data, ttl);
    }
    
    /**
     * Get cached data with sliding expiration
     */
    public Object getCachedDataWithSlidingExpiration(String key) {
        return redisTemplate.opsForValue().get("sliding:" + key);
    }
    
    /**
     * Clear all caches for a specific pattern
     */
    public void clearCachePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
    
    /**
     * Get cache statistics
     */
    public Object getCacheStatistics() {
        return redisTemplate.getConnectionFactory().getConnection().info("memory");
    }
}
