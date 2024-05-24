package org.hackncrypt.paymentservice.controller.payment;

import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.model.dto.request.CreateCustomerRequest;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;
import org.hackncrypt.paymentservice.model.dto.response.UserSubscriptionDto;
import org.hackncrypt.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user/payment")
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/payment-intent/setup")
    public ResponseEntity<CreateSubscriptionResponse> createSubscription(@RequestBody CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
        log.info("Creating Subscription for userId :  {}",createSubscriptionRequest.getUserId());
        return ResponseEntity.ok(paymentService.createSubscription(createSubscriptionRequest));
    }
    @GetMapping("/subscriptions")
    public ResponseEntity<List<UserSubscriptionDto>> getUserSubscriptions(HttpServletRequest request){
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        return ResponseEntity.ok(paymentService.getUserSubscriptions(userId));
    }

}
