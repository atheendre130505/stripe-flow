package com.stripeflow.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis caching configuration for high-performance caching strategies
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis cache manager with multiple cache configurations
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Cache-specific configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Customer cache - 1 hour TTL
        cacheConfigurations.put("customers", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Charge cache - 30 minutes TTL
        cacheConfigurations.put("charges", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Refund cache - 1 hour TTL
        cacheConfigurations.put("refunds", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Subscription cache - 2 hours TTL
        cacheConfigurations.put("subscriptions", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // Webhook cache - 15 minutes TTL
        cacheConfigurations.put("webhooks", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Statistics cache - 5 minutes TTL
        cacheConfigurations.put("statistics", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Session cache - 24 hours TTL
        cacheConfigurations.put("sessions", defaultConfig.entryTtl(Duration.ofHours(24)));
        
        // API key cache - 1 hour TTL
        cacheConfigurations.put("apiKeys", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
