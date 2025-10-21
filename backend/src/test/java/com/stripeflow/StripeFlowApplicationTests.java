package com.stripeflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Main application test to verify Spring Boot context loads correctly
 */
@SpringBootTest
@ActiveProfiles("test")
class StripeFlowApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        // with all the configured beans and dependencies
    }
}

