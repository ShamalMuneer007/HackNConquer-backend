package org.hackncrypt.paymentservice.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookController {
    private final PaymentService paymentService;
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhookEvent(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        paymentService.handleWebhookEvent(payload,sigHeader);
        return ResponseEntity.status(HttpStatus.OK).body("Received");
    }
}
