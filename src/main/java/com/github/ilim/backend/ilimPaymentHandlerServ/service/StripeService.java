package com.github.ilim.backend.ilimPaymentHandlerServ.service;


import com.github.ilim.backend.ilimPaymentHandlerServ.model.CheckoutRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    private final String successUrl;
    private final String cancelUrl;

    public StripeService(@Value("${app.success.url}") String successUrl,
                         @Value("${app.cancel.url}") String cancelUrl) {
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }

    public String createCheckoutSession(CheckoutRequest request) throws StripeException {
        logger.info("Creating Stripe checkout session for user: {}", request.getUserId());

        // Convert course price to the smallest currency unit (e.g., cents for USD)
        long amountInCents = Math.round(request.getCoursePrice() * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(request.getCurrency())
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(request.getCourseName())
                                                                .setDescription(request.getCourseDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                // Add custom metadata
                .putMetadata("userId", request.getUserId())
                .putMetadata("courseId", String.valueOf(request.getCourseId()))
                .putMetadata("courseName", request.getCourseName())
                .putMetadata("courseDescription", request.getCourseDescription())
                .build();

        Session session = Session.create(params);
        logger.info("Stripe checkout session created successfully: {}", session.getId());
        return session.getUrl();
    }
}
