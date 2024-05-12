package org.hackncrypt.paymentservice.service;

import com.stripe.exception.StripeException;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;

public interface PaymentService {
    CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest) throws StripeException;

    void handleWebhookEvent(String payload, String sigHeader) throws StripeException;
}
