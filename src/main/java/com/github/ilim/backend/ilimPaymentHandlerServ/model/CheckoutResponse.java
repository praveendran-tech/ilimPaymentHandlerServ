package com.github.ilim.backend.ilimPaymentHandlerServ.model;

public class CheckoutResponse {
    private String stripeUrl;

    public CheckoutResponse() {
    }

    public CheckoutResponse(String stripeUrl) {
        this.stripeUrl = stripeUrl;
    }

    public String getStripeUrl() {
        return stripeUrl;
    }

    public void setStripeUrl(String stripeUrl) {
        this.stripeUrl = stripeUrl;
    }
}