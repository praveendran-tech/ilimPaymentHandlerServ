package com.github.ilim.backend.ilimPaymentHandlerServ.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class StripeConfig {
    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);

    // Stripe API Key
    @Value("${stripe.api.key}")
    private String apiKey;

    // Webhook Configuration
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.webhook.endpoint}")
    private String webhookEndpoint;

    // Getters and Setters
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public String getWebhookEndpoint() {
        return webhookEndpoint;
    }

    public void setWebhookEndpoint(String webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("Stripe API key is not set. Please set 'stripe.api.key' in application.properties.");
            throw new IllegalStateException("Stripe API key is not set.");
        }
        Stripe.apiKey = apiKey;
        logger.info("Stripe API key initialized successfully.");
    }
}