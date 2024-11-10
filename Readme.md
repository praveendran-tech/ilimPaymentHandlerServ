# IlimPaymentHandlerServ

## Overview

IlimPaymentHandlerServ is a Spring Boot application that manages payment processing through Stripe and publishes transaction updates via Kafka. This project is designed to handle secure payment requests, process webhooks from Stripe, and ensure smooth communication with other services using Kafka.

## Project Structure

The main components of the project include:

- **CheckoutController.java**: Handles payment initiation requests from clients, processes `CheckoutRequest`, and returns a `CheckoutResponse`.
- **StripeService.java**: A service to interact with the Stripe API for processing payments and managing customer details.
- **KafkaPublisherService.java**: Responsible for publishing payment status updates to a Kafka queue.
- **GlobalExceptionHandler.java**: Provides centralized exception handling across the application.
- **StripeConfig.java**: Contains configuration settings for Stripe, such as API keys and endpoints.
- **KafkaProducerConfig.java**: Manages Kafka producer settings for reliable message delivery.
- **StripeWebhookController.java**: Handles incoming webhooks from Stripe to update payment statuses or other relevant information.
- **IlimPaymentHandlerServApplication.java**: The main application class that bootstraps the Spring Boot application.

## Features

- **Stripe Payment Processing**: Integrates with Stripe to securely process payments.
- **Kafka Event Publishing**: Publishes payment events to a Kafka topic for downstream services.
- **Webhook Handling**: Processes Stripe webhooks for real-time updates on payment status.
- **Global Exception Handling**: Manages application-wide exceptions to ensure reliability.

## Installation

### Prerequisites

- Java 11 or higher
- Apache Kafka
- Stripe Account with API keys

### Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/praveendran-tech/ilimPaymentHandlerServ.git
   cd ilimPaymentHandlerServ
