package com.github.ilim.backend.ilimPaymentHandlerServ.controller;


import com.github.ilim.backend.ilimPaymentHandlerServ.model.CheckoutRequest;
import com.github.ilim.backend.ilimPaymentHandlerServ.model.CheckoutResponse;
import com.github.ilim.backend.ilimPaymentHandlerServ.service.StripeService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final StripeService stripeService;

    @Autowired
    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<CheckoutResponse> createCheckoutSession(@Valid @RequestBody CheckoutRequest request) throws StripeException {
        String stripeUrl = stripeService.createCheckoutSession(request);
        CheckoutResponse response = new CheckoutResponse(stripeUrl);
        return ResponseEntity.ok(response);
    }
}