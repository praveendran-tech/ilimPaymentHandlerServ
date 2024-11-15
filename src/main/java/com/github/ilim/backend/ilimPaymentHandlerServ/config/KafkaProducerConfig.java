package com.github.ilim.backend.ilimPaymentHandlerServ.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${kafka.producer.buffer.memory}")
    private Long bufferMemory;

    @Value("${kafka.producer.batch.size}")
    private Integer batchSize;

    @Value("${kafka.producer.linger.ms}")
    private Integer lingerMs;

    @Value("${kafka.producer.compression.type}")
    private String compressionType;

    @Value("${kafka.producer.retries}")
    private Integer retries;

    @Value("${kafka.producer.max.in.flight.requests.per.connection}")
    private Integer maxInFlightRequests;

    @Value("${kafka.producer.acks}")
    private String acks;

    @Value("${kafka.producer.enable.idempotence}")
    private Boolean enableIdempotence;

    @Value("${kafka.producer.max.request.size}")
    private Integer maxRequestSize;

    @Value("${kafka.producer.max.block.ms}")
    private Integer maxBlockMs;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Basic Producer Properties
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        // Memory and Batching
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);

        // Compression
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);

        // Retries and Acknowledgments
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequests);
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);

        // Idempotence
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        // Additional Settings to Control Throughput
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
