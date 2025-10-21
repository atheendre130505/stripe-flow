package com.stripeflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for StripeFlow Payment Integration Sandbox
 * 
 * Features:
 * - Payment processing APIs (charges, refunds, subscriptions)
 * - Customer management
 * - Webhook system with retry logic
 * - Fraud detection engine
 * - Multi-currency support
 * - Real-time dashboard
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class StripeFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripeFlowApplication.class, args);
    }
}

