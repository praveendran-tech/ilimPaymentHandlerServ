package com.github.ilim.backend.ilimPaymentHandlerServ.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.ExecutionException;

@Service
public class KafkaPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisherService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate,
                                 @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * Publishes a message to the Kafka topic.
     * Includes retry logic for transient failures.
     *
     * @param key     The key for the Kafka message (e.g., Payment ID)
     * @param message The message payload (custom JSON)
     */
    @Retryable(
            value = { KafkaException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void publish(String key, String message) {
        logger.info("Publishing message to Kafka topic '{}', key: {}", topic, key);
        try {
            // Synchronously send the message
            SendResult<String, String> result = kafkaTemplate.send(topic, key, message).get();
            logger.info("Successfully published message with key {}", key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            logger.error("Thread was interrupted while publishing message with key {}", key, e);
            throw new KafkaException("Thread was interrupted during Kafka publish", e);
        } catch (ExecutionException e) {
            logger.error("Failed to publish message with key {}", key, e.getCause());
            throw new KafkaException("Failed to publish message to Kafka", e.getCause());
        }
    }

    /**
     * Recovery method invoked after all retry attempts fail.
     *
     * @param e       The exception that caused the failure
     * @param key     The key of the failed message
     * @param message The payload of the failed message
     */
    @Recover
    public void recover(KafkaException e, String key, String message) {
        logger.error("All retry attempts failed for message with key {}. Publishing to DLQ.", key, e);
        try {
            // Define DLQ topic name
            String dlqTopic = topic + ".DLQ";
            // Synchronously send the message to DLQ
            SendResult<String, String> result = kafkaTemplate.send(dlqTopic, key, message).get();
            logger.info("Message with key {} successfully published to DLQ '{}'.", key, dlqTopic);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            logger.error("Thread was interrupted while publishing to DLQ for key {}", key, ie);
            // Optionally, persist the failed message to a database or alert administrators
        } catch (ExecutionException ee) {
            logger.error("Failed to publish message to DLQ '{}': {}", topic + ".DLQ", ee.getCause().getMessage(), ee.getCause());
            // Optionally, persist the failed message to a database or alert administrators
        }
    }
}