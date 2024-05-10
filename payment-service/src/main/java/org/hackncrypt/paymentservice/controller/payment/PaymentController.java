package org.hackncrypt.paymentservice.controller.payment;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;
import org.hackncrypt.paymentservice.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user/payment")
public class PaymentController {
    private final StripeService stripeService;
    @PostMapping("/payment-intent/setup")
    public ResponseEntity<CreateSubscriptionResponse> createPaymentIntent(@RequestBody CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
        log.info("Creating Subscription for userId :  {}",createSubscriptionRequest.getUserId());
        return ResponseEntity.ok(stripeService.setUpPayementIntent(createSubscriptionRequest));
    }
//    @PostMapping("/subscription/create")
//    public ResponseEntity<Boolean> createSubscription(@RequestBody CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
//        log.info("Creating Subscription for userId :  {}",createSubscriptionRequest.getUserId());
//        stripeService.createSubscription(createSubscriptionRequest);
//        return ResponseEntity.ok(true);
//    }
}
