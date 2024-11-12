package com.github.ilim.backend.ilimPaymentHandlerServ.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.ilimPaymentHandlerServ.config.StripeConfig;
import com.github.ilim.backend.ilimPaymentHandlerServ.service.KafkaPublisherService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    private final StripeConfig stripeConfig;
    private final KafkaPublisherService kafkaPublisherService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StripeWebhookController(StripeConfig stripeConfig, KafkaPublisherService kafkaPublisherService) {
        this.stripeConfig = stripeConfig;
        this.kafkaPublisherService = kafkaPublisherService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {
        Event event;
        try {
            // Verify the webhook signature
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeConfig.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            // Invalid signature
            logger.warn("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            // Other errors (e.g., JSON parsing errors)
            logger.warn("Invalid Stripe payload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        // Log the event type
        String eventType = event.getType();
        logger.info("Received Stripe event: {}", eventType);

        // Only handle checkout.session.completed events
        if ("checkout.session.completed".equals(eventType)) {
            try {
                // Parse the event data
                JsonNode eventData = objectMapper.readTree(event.getData().toJson());
                JsonNode objectNode = eventData.get("object");
                if (objectNode == null) {
                    logger.error("Missing 'object' node in event data");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid event data");
                }

                String paymentIntentId = objectNode.get("payment_intent").asText();

                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

                String paymentStatus = paymentIntent.getStatus();
                long createdTimestamp = paymentIntent.getCreated();
                String paymentDate = String.valueOf(createdTimestamp);

                String paymentId = objectNode.get("id").asText();
                JsonNode customerDetailsNode = objectNode.get("customer_details");
                String name = customerDetailsNode.get("name").asText();
                String email = customerDetailsNode.get("email").asText();
                JsonNode phoneNode = customerDetailsNode.get("phone");
                String mobileNumber = (phoneNode != null && !phoneNode.isNull()) ? phoneNode.asText() : "Unknown";

                // Extract metadata
                JsonNode metadataNode = objectNode.get("metadata");
                if (metadataNode == null) {
                    logger.warn("No metadata found in the session");
                }

                String userId = metadataNode != null && metadataNode.has("userId") ? metadataNode.get("userId").asText() : null;
                String courseId = metadataNode != null && metadataNode.has("courseId") ? metadataNode.get("courseId").asText() : null;

                // Construct the custom JSON to publish
                JsonNode customJsonNode = objectMapper.createObjectNode()
                        .put("userId", userId)
                        .put("courseId", courseId)
                        .put("status", paymentStatus)
                        .put("paymentDate", paymentDate)
                        .put("name", name)
                        .put("paymentId", paymentId)
                        .put("mobileNumber", mobileNumber)
                        .put("email", email);

                String customJson = objectMapper.writeValueAsString(customJsonNode);

                logger.debug("Custom JSON to publish: {}", customJson);

                // Publish to Kafka using userId as the key (or paymentId if preferred)
                kafkaPublisherService.publish(paymentId, customJson);

            } catch (Exception e) {
                logger.error("Failed to process checkout.session.completed event: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process event");
            }
        } else {
            logger.info("Unhandled event type: {}", eventType);
        }

        // Acknowledge receipt of the event
        return ResponseEntity.ok("Webhook received");
    }
}
