package org.hackncrypt.paymentservice.service;

import com.stripe.exception.StripeException;
import org.hackncrypt.paymentservice.model.dto.GetTotalRevenueResponse;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;
import org.hackncrypt.paymentservice.model.dto.response.GetRevenueResponse;
import org.hackncrypt.paymentservice.model.dto.response.UserSubscriptionDto;

import java.util.List;

public interface PaymentService {
    CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest) throws StripeException;

    void handleWebhookEvent(String payload, String sigHeader) throws StripeException;

    List<UserSubscriptionDto> getUserSubscriptions(Long userId);

    List<GetRevenueResponse> getAllSubscriptions(String interval);

    GetTotalRevenueResponse getTotalRevenue();
}
