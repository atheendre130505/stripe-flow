package com.stripeflow.scheduler;

import com.stripeflow.model.WebhookEvent;
import com.stripeflow.repository.WebhookEventRepository;
import com.stripeflow.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler for processing webhook retries
 */
@Component
public class WebhookRetryScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookRetryScheduler.class);
    
    @Autowired
    private WebhookEventRepository webhookEventRepository;
    
    @Autowired
    private WebhookService webhookService;
    
    /**
     * Process webhook retries every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    @Transactional
    public void processWebhookRetries() {
        try {
            List<WebhookEvent> eventsReadyForRetry = webhookEventRepository.findEventsReadyForRetry(LocalDateTime.now());
            
            if (!eventsReadyForRetry.isEmpty()) {
                logger.info("Processing {} webhook events for retry", eventsReadyForRetry.size());
                
                for (WebhookEvent event : eventsReadyForRetry) {
                    try {
                        webhookService.processWebhookDelivery(event);
                    } catch (Exception e) {
                        logger.error("Error processing webhook retry for event {}: {}", 
                            event.getId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in webhook retry scheduler: {}", e.getMessage());
        }
    }
    
    /**
     * Clean up old webhook events daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Transactional
    public void cleanupOldWebhookEvents() {
        try {
            // Delete webhook events older than 30 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            List<WebhookEvent> eventsToDelete = webhookEventRepository.findEventsForCleanup(cutoffDate);
            
            if (!eventsToDelete.isEmpty()) {
                logger.info("Cleaning up {} old webhook events", eventsToDelete.size());
                webhookEventRepository.deleteAll(eventsToDelete);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up old webhook events: {}", e.getMessage());
        }
    }
    
    /**
     * Generate webhook statistics daily at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * ?") // Daily at 1 AM
    public void generateWebhookStatistics() {
        try {
            long totalEvents = webhookEventRepository.count();
            long pendingEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.PENDING);
            long deliveredEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.DELIVERED);
            long failedEvents = webhookEventRepository.countEventsByStatus(WebhookEvent.WebhookEventStatus.FAILED);
            
            logger.info("Webhook Statistics - Total: {}, Pending: {}, Delivered: {}, Failed: {}", 
                totalEvents, pendingEvents, deliveredEvents, failedEvents);
        } catch (Exception e) {
            logger.error("Error generating webhook statistics: {}", e.getMessage());
        }
    }
}



