package com.stripeflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * OpenAPI/Swagger configuration for comprehensive API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .tags(tags())
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    private Info apiInfo() {
        return new Info()
                .title("StripeFlow API")
                .description("""
                    # StripeFlow Payment Processing API
                    
                    A comprehensive payment processing platform that provides secure, scalable, and reliable payment solutions.
                    
                    ## Features
                    - **Payment Processing**: Create and manage payment charges
                    - **Customer Management**: Complete customer lifecycle management
                    - **Refund Processing**: Automated refund handling
                    - **Subscription Management**: Recurring payment processing
                    - **Webhook System**: Real-time event notifications
                    - **Multi-currency Support**: Global payment processing
                    
                    ## Authentication
                    All API endpoints require authentication using API keys. Include your API key in the `X-API-Key` header.
                    
                    ## Rate Limiting
                    API requests are rate limited to 100 requests per minute per API key.
                    
                    ## Webhooks
                    Configure webhook endpoints to receive real-time notifications about payment events.
                    
                    ## Support
                    For support and questions, contact our team at support@stripeflow.com
                    """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("StripeFlow Support")
                        .email("support@stripeflow.com")
                        .url("https://stripeflow.com/support"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("https://api.stripeflow.com")
                        .description("Production Server"),
                new Server()
                        .url("https://api-staging.stripeflow.com")
                        .description("Staging Server"),
                new Server()
                        .url("http://localhost:8080")
                        .description("Development Server")
        );
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("Customers").description("Customer management operations"),
                new Tag().name("Charges").description("Payment charge operations"),
                new Tag().name("Refunds").description("Refund processing operations"),
                new Tag().name("Subscriptions").description("Subscription management operations"),
                new Tag().name("Webhooks").description("Webhook management operations"),
                new Tag().name("Analytics").description("Analytics and reporting operations"),
                new Tag().name("Health").description("Health check and monitoring operations")
        );
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("ApiKey", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                        .description("API Key for authentication"))
                .addSchemas("ErrorResponse", errorResponseSchema())
                .addSchemas("ValidationError", validationErrorSchema())
                .addSchemas("PaginationResponse", paginationResponseSchema())
                .addSchemas("HealthStatus", healthStatusSchema())
                .addSchemas("WebhookEvent", webhookEventSchema())
                .addSchemas("ChargeStatistics", chargeStatisticsSchema())
                .addSchemas("WebhookStatistics", webhookStatisticsSchema());
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("ApiKey");
    }

    private Schema<?> errorResponseSchema() {
        return new ObjectSchema()
                .addProperty("timestamp", new DateTimeSchema().example("2023-01-01T00:00:00Z"))
                .addProperty("status", new IntegerSchema().example(400))
                .addProperty("error", new StringSchema().example("Bad Request"))
                .addProperty("message", new StringSchema().example("Invalid request parameters"))
                .addProperty("path", new StringSchema().example("/api/v1/charges"))
                .addProperty("errors", new ArraySchema().items(new ObjectSchema()
                        .addProperty("field", new StringSchema().example("amount"))
                        .addProperty("message", new StringSchema().example("Amount must be greater than 0"))));
    }

    private Schema<?> validationErrorSchema() {
        return new ObjectSchema()
                .addProperty("field", new StringSchema().example("email"))
                .addProperty("message", new StringSchema().example("Email format is invalid"))
                .addProperty("rejectedValue", new StringSchema().example("invalid-email"));
    }

    private Schema<?> paginationResponseSchema() {
        return new ObjectSchema()
                .addProperty("content", new ArraySchema().items(new ObjectSchema()))
                .addProperty("totalElements", new IntegerSchema().example(100))
                .addProperty("totalPages", new IntegerSchema().example(10))
                .addProperty("size", new IntegerSchema().example(20))
                .addProperty("number", new IntegerSchema().example(0))
                .addProperty("first", new BooleanSchema().example(true))
                .addProperty("last", new BooleanSchema().example(false));
    }

    private Schema<?> healthStatusSchema() {
        return new ObjectSchema()
                .addProperty("status", new StringSchema().example("UP"))
                .addProperty("components", new ObjectSchema()
                        .addProperty("db", new ObjectSchema()
                                .addProperty("status", new StringSchema().example("UP"))
                                .addProperty("details", new ObjectSchema()
                                        .addProperty("database", new StringSchema().example("PostgreSQL"))
                                        .addProperty("validationQuery", new StringSchema().example("SELECT 1"))))
                        .addProperty("redis", new ObjectSchema()
                                .addProperty("status", new StringSchema().example("UP"))
                                .addProperty("details", new ObjectSchema()
                                        .addProperty("version", new StringSchema().example("7.0.0")))));
    }

    private Schema<?> webhookEventSchema() {
        return new ObjectSchema()
                .addProperty("id", new StringSchema().example("evt_1234567890"))
                .addProperty("type", new StringSchema().example("charge.succeeded"))
                .addProperty("data", new ObjectSchema()
                        .addProperty("object", new ObjectSchema()
                                .addProperty("id", new StringSchema().example("ch_1234567890"))
                                .addProperty("amount", new IntegerSchema().example(1000))
                                .addProperty("currency", new StringSchema().example("usd"))
                                .addProperty("status", new StringSchema().example("succeeded"))))
                .addProperty("created", new IntegerSchema().example(1640995200))
                .addProperty("livemode", new BooleanSchema().example(false));
    }

    private Schema<?> chargeStatisticsSchema() {
        return new ObjectSchema()
                .addProperty("totalCharges", new IntegerSchema().example(1000))
                .addProperty("successfulCharges", new IntegerSchema().example(950))
                .addProperty("failedCharges", new IntegerSchema().example(50))
                .addProperty("successRate", new NumberSchema().example(95.0))
                .addProperty("totalRevenue", new NumberSchema().example(100000.00))
                .addProperty("averageAmount", new NumberSchema().example(100.00))
                .addProperty("currencyBreakdown", new ObjectSchema()
                        .addProperty("USD", new IntegerSchema().example(800))
                        .addProperty("EUR", new IntegerSchema().example(150))
                        .addProperty("GBP", new IntegerSchema().example(50)));
    }

    private Schema<?> webhookStatisticsSchema() {
        return new ObjectSchema()
                .addProperty("totalEndpoints", new IntegerSchema().example(5))
                .addProperty("enabledEndpoints", new IntegerSchema().example(3))
                .addProperty("totalEvents", new IntegerSchema().example(10000))
                .addProperty("deliveredEvents", new IntegerSchema().example(9500))
                .addProperty("failedEvents", new IntegerSchema().example(500))
                .addProperty("deliverySuccessRate", new NumberSchema().example(95.0))
                .addProperty("averageDeliveryTime", new NumberSchema().example(150.5));
    }
}