package org.hackncrypt.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionLineItemListParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeService {
    @Value("${stripe.api.secretKey}")
    private String API_SECRET_KEY;

    @PostConstruct
    public void init(){
        log.info("SECRET  API : {}",API_SECRET_KEY);
    }
    public String createCustomer(String email, String name) throws StripeException{
        String id = null;
            Stripe.apiKey = API_SECRET_KEY;

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name)
                .setDescription("CUSTOMER EMAIL : "+email)
                .build();

        Customer customer = Customer.create(params);
        id = customer.getId();
        return id;
    }

    public CreateSubscriptionResponse setUpPayementIntent(CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
        Stripe.apiKey = API_SECRET_KEY;
        log.info("request : {}",createSubscriptionRequest);
        // Retrieve the PaymentMethod
        String paymentMethodId = createSubscriptionRequest.getPaymentMethod();

        String priceId = createSubscriptionRequest.getPriceId();

        // Create a Customer with the SetupIntent's PaymentMethod as the default payment method
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(createSubscriptionRequest.getName())
                .setEmail(createSubscriptionRequest.getEmail())
                .build();
        Customer customer = Customer.create(customerParams);
        log.info("Created Customer, ID: {}", customer.getId());
//
        PaymentMethod resource = PaymentMethod.retrieve(createSubscriptionRequest.getPaymentMethod());
        Price price = Price.retrieve(createSubscriptionRequest.getPriceId());
//
        PaymentMethodAttachParams params =
                PaymentMethodAttachParams.builder().setCustomer(customer.getId()).build();
        log.info("PAYMENT METHOD ATTACH PARAM : {}",params);
        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams.builder()
                        .setAmount(price.getUnitAmount())
                        .setCurrency("inr")
                        .setCustomer(customer.getId())
                        .putExtraParam("setup_future_usage","on_session")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .setPaymentMethod("pm_card_visa_in")
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
        PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .setSetupFutureUsage(PaymentIntentConfirmParams.SetupFutureUsage.ON_SESSION)
                        .setReturnUrl("http://localhost:5173")
                        .build()
        );
//        log.info("CONFIRM PAYMENT INTENT PAYMENT METHOD : {} , {}",confirmedPaymentIntent,confirmedPaymentIntent.getPaymentMethodObject());

//        confirmedPaymentIntent.getPaymentMethodObject().attach(paymentMethodAttachParams);
//        SetupIntentCreateParams setupIntentParams = SetupIntentCreateParams.builder()
//                .setPaymentMethod(resource.getId())
//                .addPaymentMethodType("card")
//                .setCustomer(customer.getId())
//                .build();
//        SetupIntent setupIntent = SetupIntent.create(setupIntentParams);
//        SetupIntent confirmedSetupIntent = setupIntent.confirm(
//                SetupIntentConfirmParams.builder()
//                        .setPaymentMethod(paymentMethodId)
//                        .build()
//        );
//        log.info("CONFIRM PAYMENT INTEND :{}",confirmedPaymentIntent);
//        log.info("CONFIRM SETUP INTENT : {}",confirmedSetupIntent);
//        log.info("Payment METHOD : {} | {}",confirmedSetupIntent.getPaymentMethod(),resource.getId());
//        // Retrieve the price ID
//        log.info("PRICE ID : {}", priceId);
//
//        // Create Subscription parameters with 3D Secure authentication and card payment method type
        log.info("Creating subscription for customer : {}",customer.getId());
        SubscriptionCreateParams.PaymentSettings paymentSettings =
                SubscriptionCreateParams.PaymentSettings
                        .builder()
                        .setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
                        .build();

        // Create the subscription. Note we're expanding the Subscription's
        // latest invoice and that invoice's payment_intent
        // so we can pass it to the front end to confirm the payment
        SubscriptionCreateParams subCreateParams = SubscriptionCreateParams
                .builder()
                .setCustomer(customer.getId())
                .addItem(
                        SubscriptionCreateParams
                                .Item.builder()
                                .setPrice(createSubscriptionRequest.getPriceId())
                                .build()
                )
                .setPaymentSettings(paymentSettings)
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(Arrays.asList("latest_invoice.payment_intent"))
                .build();

        Subscription subscription = Subscription.create(subCreateParams);
        log.info("Created Subscription, ID : {}", subscription.getId());

        // Return the client secret and subscription ID
        return new CreateSubscriptionResponse(confirmedPaymentIntent.getClientSecret(),customer.getId(),subscription.getId());
    }
//    public CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
//
//
//    }




    public void cancelSubscription(String subscriptionId) throws StripeException{
        Subscription subscription = Subscription.retrieve(subscriptionId);
        subscription.cancel();
    }
    public String createCheckoutSession(String customerId, String subscriptionPriceId) throws StripeException {
        Stripe.apiKey = API_SECRET_KEY;
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPrice(subscriptionPriceId)
                .setQuantity(1L)
                .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCurrency("inr")
                        .setCustomer(customerId)
                        .addLineItem(lineItem)
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .build();
 
        Session session = Session.create(params);
        return session.getId();
    }

    public String createCharge(String email, String token, int amount) throws StripeException{

        String chargeId = null;
        Stripe.apiKey = API_SECRET_KEY;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("description","Charge for "+email);
        chargeParams.put("currency","usd");
        chargeParams.put("amount",amount);
        chargeParams.put("source",token);

        Charge charge = Charge.create(chargeParams);

        chargeId = charge.getId();

        return chargeId;
    }
}
