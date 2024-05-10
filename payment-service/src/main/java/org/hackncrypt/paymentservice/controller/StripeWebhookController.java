package org.hackncrypt.paymentservice.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/stripe")
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookController {
    private final StripeService stripeService;
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhookEvent(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) {
        // Retrieve your Stripe webhook secret from your environment variables
        String webhookSecret = System.getenv("stripe.webhook.secretKey");
        log.info("WEBHOOK SECRET : {}",webhookSecret);
        // Verify the webhook signature
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            // Handle the event
            if ("customer.subscription.deleted".equals(event.getType())) {
                Subscription subscription =  (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
                // Handle subscription cancellation
                handleSubscriptionCancellation(subscription.getId());
            }

            // Return a 200 response to acknowledge receipt of the event
            return ResponseEntity.status(HttpStatus.OK).body("Received");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }

    // Method to handle subscription cancellation
    private void handleSubscriptionCancellation(String subscriptionId) {
        // Logic to handle subscription cancellation, e.g., update
        try {
            stripeService.cancelSubscription(subscriptionId);
        }
        catch (StripeException e){
            log.error("Error While cancelling subscription : {}",e.getMessage());
        }
        System.out.println("Subscription canceled: " + subscriptionId);
    }
}
