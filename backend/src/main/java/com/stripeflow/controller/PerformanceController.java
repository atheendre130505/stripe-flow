package com.stripeflow.controller;

import com.stripeflow.service.CacheService;
import com.stripeflow.service.OptimizedChargeService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance testing and monitoring controller
 */
@RestController
@RequestMapping("/api/v1/performance")
public class PerformanceController {
    
    @Autowired
    private OptimizedChargeService chargeService;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    private final AtomicLong requestCounter = new AtomicLong(0);
    private final AtomicLong successCounter = new AtomicLong(0);
    private final AtomicLong failureCounter = new AtomicLong(0);
    
    /**
     * Load test endpoint for performance testing
     */
    @PostMapping("/load-test")
    public ResponseEntity<Map<String, Object>> loadTest(
            @RequestParam(defaultValue = "100") int requests,
            @RequestParam(defaultValue = "10") int concurrency) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Simulate load test
            CompletableFuture<Void>[] futures = new CompletableFuture[concurrency];
            
            for (int i = 0; i < concurrency; i++) {
                futures[i] = CompletableFuture.runAsync(() -> {
                    for (int j = 0; j < requests / concurrency; j++) {
                        try {
                            // Simulate API call
                            Thread.sleep(10); // Simulate 10ms processing time
                            successCounter.incrementAndGet();
                        } catch (InterruptedException e) {
                            failureCounter.incrementAndGet();
                            Thread.currentThread().interrupt();
                        }
                        requestCounter.incrementAndGet();
                    }
                });
            }
            
            // Wait for all requests to complete
            CompletableFuture.allOf(futures).join();
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            results.put("totalRequests", requestCounter.get());
            results.put("successfulRequests", successCounter.get());
            results.put("failedRequests", failureCounter.get());
            results.put("totalTime", totalTime + "ms");
            results.put("requestsPerSecond", (requestCounter.get() * 1000.0) / totalTime);
            results.put("averageResponseTime", (totalTime * 1.0) / requestCounter.get() + "ms");
            results.put("concurrency", concurrency);
            results.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            results.put("error", e.getMessage());
            return ResponseEntity.status(500).body(results);
        }
    }
    
    /**
     * Cache performance test
     */
    @GetMapping("/cache-test")
    public ResponseEntity<Map<String, Object>> cacheTest(
            @RequestParam(defaultValue = "1000") int iterations) {
        
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test cache performance
            for (int i = 0; i < iterations; i++) {
                String key = "test_key_" + i;
                String value = "test_value_" + i;
                
                // Cache the value
                cacheService.cacheFrequentData(key, value, Duration.ofMinutes(1));
                
                // Retrieve the value
                Object retrievedValue = cacheService.getCachedFrequentData(key);
                
                if (retrievedValue == null) {
                    failureCounter.incrementAndGet();
                } else {
                    successCounter.incrementAndGet();
                }
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            results.put("iterations", iterations);
            results.put("successfulOperations", successCounter.get());
            results.put("failedOperations", failureCounter.get());
            results.put("totalTime", totalTime + "ms");
            results.put("operationsPerSecond", (iterations * 1000.0) / totalTime);
            results.put("averageOperationTime", (totalTime * 1.0) / iterations + "ms");
            results.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            results.put("error", e.getMessage());
            return ResponseEntity.status(500).body(results);
        }
    }
    
    /**
     * Database performance test
     */
    @GetMapping("/database-test")
    public ResponseEntity<Map<String, Object>> databaseTest(
            @RequestParam(defaultValue = "100") int iterations) {
        
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test database performance
            for (int i = 0; i < iterations; i++) {
                try {
                    // Simulate database query
                    Thread.sleep(5); // Simulate 5ms database query time
                    successCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    failureCounter.incrementAndGet();
                    Thread.currentThread().interrupt();
                }
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            results.put("iterations", iterations);
            results.put("successfulQueries", successCounter.get());
            results.put("failedQueries", failureCounter.get());
            results.put("totalTime", totalTime + "ms");
            results.put("queriesPerSecond", (iterations * 1000.0) / totalTime);
            results.put("averageQueryTime", (totalTime * 1.0) / iterations + "ms");
            results.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            results.put("error", e.getMessage());
            return ResponseEntity.status(500).body(results);
        }
    }
    
    /**
     * Get system performance metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // System metrics
        Runtime runtime = Runtime.getRuntime();
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        metrics.put("memoryUsage", ((runtime.totalMemory() - runtime.freeMemory()) * 100.0) / runtime.maxMemory());
        
        // Thread metrics
        ThreadMXBean threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
        metrics.put("activeThreads", threadBean.getThreadCount());
        metrics.put("peakThreads", threadBean.getPeakThreadCount());
        metrics.put("daemonThreads", threadBean.getDaemonThreadCount());
        
        // GC metrics
        java.lang.management.GarbageCollectorMXBean[] gcBeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        for (java.lang.management.GarbageCollectorMXBean gcBean : gcBeans) {
            metrics.put("gc_" + gcBean.getName() + "_count", gcBean.getCollectionCount());
            metrics.put("gc_" + gcBean.getName() + "_time", gcBean.getCollectionTime());
        }
        
        // Application metrics
        metrics.put("totalRequests", requestCounter.get());
        metrics.put("successfulRequests", successCounter.get());
        metrics.put("failedRequests", failureCounter.get());
        metrics.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Reset performance counters
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetCounters() {
        requestCounter.set(0);
        successCounter.set(0);
        failureCounter.set(0);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Performance counters reset successfully");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check with performance metrics
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic health indicators
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Performance indicators
        double memoryUsage = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * 100.0) / Runtime.getRuntime().maxMemory();
        health.put("memoryUsage", memoryUsage + "%");
        health.put("activeThreads", java.lang.management.ManagementFactory.getThreadMXBean().getThreadCount());
        health.put("systemLoad", java.lang.management.ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
        
        // Determine health status based on metrics
        if (memoryUsage > 90) {
            health.put("status", "DEGRADED");
            health.put("warning", "High memory usage detected");
        }
        
        if (java.lang.management.ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() > 4.0) {
            health.put("status", "DEGRADED");
            health.put("warning", "High system load detected");
        }
        
        return ResponseEntity.ok(health);
    }
}


