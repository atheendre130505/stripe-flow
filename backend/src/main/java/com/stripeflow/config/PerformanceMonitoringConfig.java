package com.stripeflow.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

/**
 * Performance monitoring configuration for high-throughput systems
 */
@Configuration
public class PerformanceMonitoringConfig {
    
    /**
     * Payment processing metrics
     */
    @Bean
    public Counter paymentSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.payments.success")
                .description("Number of successful payments")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter paymentFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.payments.failure")
                .description("Number of failed payments")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer paymentProcessingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("stripeflow.payments.processing.time")
                .description("Payment processing time")
                .register(meterRegistry);
    }
    
    /**
     * Webhook delivery metrics
     */
    @Bean
    public Counter webhookSuccessCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.webhooks.success")
                .description("Number of successful webhook deliveries")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter webhookFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.webhooks.failure")
                .description("Number of failed webhook deliveries")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer webhookDeliveryTimer(MeterRegistry meterRegistry) {
        return Timer.builder("stripeflow.webhooks.delivery.time")
                .description("Webhook delivery time")
                .register(meterRegistry);
    }
    
    /**
     * Cache performance metrics
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.cache.hits")
                .description("Number of cache hits")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter cacheMissCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.cache.misses")
                .description("Number of cache misses")
                .register(meterRegistry);
    }
    
    /**
     * Database performance metrics
     */
    @Bean
    public Timer databaseQueryTimer(MeterRegistry meterRegistry) {
        return Timer.builder("stripeflow.database.query.time")
                .description("Database query execution time")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter databaseConnectionCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.database.connections")
                .description("Database connection usage")
                .register(meterRegistry);
    }
    
    /**
     * API performance metrics
     */
    @Bean
    public Timer apiResponseTimer(MeterRegistry meterRegistry) {
        return Timer.builder("stripeflow.api.response.time")
                .description("API response time")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter apiRequestCounter(MeterRegistry meterRegistry) {
        return Counter.builder("stripeflow.api.requests")
                .description("Number of API requests")
                .register(meterRegistry);
    }
    
    /**
     * System performance metrics
     */
    @Bean
    public Gauge systemLoadGauge(MeterRegistry meterRegistry) {
        return Gauge.builder("stripeflow.system.load")
                .description("System load average")
                .register(meterRegistry, this, PerformanceMonitoringConfig::getSystemLoad);
    }
    
    @Bean
    public Gauge memoryUsageGauge(MeterRegistry meterRegistry) {
        return Gauge.builder("stripeflow.system.memory.usage")
                .description("Memory usage percentage")
                .register(meterRegistry, this, PerformanceMonitoringConfig::getMemoryUsage);
    }
    
    @Bean
    public Gauge threadPoolGauge(MeterRegistry meterRegistry) {
        return Gauge.builder("stripeflow.system.threads.active")
                .description("Active thread count")
                .register(meterRegistry, this, PerformanceMonitoringConfig::getActiveThreadCount);
    }
    
    /**
     * Get system load average
     */
    private double getSystemLoad() {
        return java.lang.management.ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }
    
    /**
     * Get memory usage percentage
     */
    private double getMemoryUsage() {
        java.lang.management.MemoryMXBean memoryBean = java.lang.management.ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        return (double) usedMemory / maxMemory * 100;
    }
    
    /**
     * Get active thread count
     */
    private double getActiveThreadCount() {
        return java.lang.management.ManagementFactory.getThreadMXBean().getThreadCount();
    }
}
