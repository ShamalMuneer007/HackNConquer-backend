package org.hackncrypt.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.config.rabbitMQ.MQConfig;
import org.hackncrypt.paymentservice.model.dto.GetTotalRevenueResponse;
import org.hackncrypt.paymentservice.model.dto.request.ChangeUserPremiumStatusRequest;
import org.hackncrypt.paymentservice.model.dto.request.CreateSubscriptionRequest;
import org.hackncrypt.paymentservice.model.dto.response.CreateSubscriptionResponse;
import org.hackncrypt.paymentservice.model.dto.response.GetRevenueResponse;
import org.hackncrypt.paymentservice.model.dto.response.UserSubscriptionDto;
import org.hackncrypt.paymentservice.model.entity.UserSubscription;
import org.hackncrypt.paymentservice.repository.UserSubscriptionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements PaymentService {
    @Value("${stripe.api.secretKey}")
    private String API_SECRET_KEY;
    @Value("${stripe.webhook.secretKey}")
    private String webhookSecret;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final RabbitTemplate rabbitTemplate;
public CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {
    Stripe.apiKey = API_SECRET_KEY;
    log.info("request : {}",createSubscriptionRequest);

    String priceId = createSubscriptionRequest.getPriceId();

    CustomerCreateParams customerParams = CustomerCreateParams.builder()
            .setName(createSubscriptionRequest.getName())
            .setEmail(createSubscriptionRequest.getEmail())
            .putMetadata("userId", String.valueOf(createSubscriptionRequest.getUserId()))
            .build();
    Customer customer = Customer.create(customerParams);
    log.info("Created Customer, ID: {}", customer.getId());
    SessionCreateParams params = new SessionCreateParams.Builder()
            .setSuccessUrl("http://localhost:5173/payment/success")
            .setCancelUrl("http://localhost:5173/payment/canceled")
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setCustomer(customer.getId())
            .addLineItem(new SessionCreateParams.LineItem.Builder()
                    .setQuantity(1L)
                    .setPrice(priceId)
                    .build()
            )
            .build();
    Session session = Session.create(params);
    log.info("CURRENT SESSION : {}",session);
    return new CreateSubscriptionResponse(session.getUrl());
}


@Transactional
    protected void cancelUserSubscription(Subscription subscription) throws StripeException {
        UserSubscription userSubscription = userSubscriptionRepository.findBySubscriptionId(subscription.getId()).orElseThrow();
        userSubscription.setSubscriptionCancelled(true);
        userSubscription.setSubscriptionCancelledAt(LocalDateTime.now());
        log.info("Cancelling subscription");
        userSubscriptionRepository.save(userSubscription);
        log.info("Sending user message");
        rabbitTemplate.convertSendAndReceive(MQConfig.USER_EXCHANGE,
                MQConfig.USER_ROUTING_KEY, new ChangeUserPremiumStatusRequest(userSubscription.getUserId(),false));
    }
    @Transactional
    protected void createUserSubscription(Subscription subscription) throws  StripeException{
        Customer customer = Customer.retrieve(subscription.getCustomer());
        Map<String, String> metadata = customer.getMetadata();
        Long userId = Long.parseLong(metadata.get("userId"));
        log.info("Creating subscription for user : {}",userId);
        UserSubscription userSubscription = UserSubscription.builder()
                .subscriptionId(subscription.getId())
                .customerId(subscription.getCustomer())
                .currency("INR")
                .amount(15F)
                .userId(userId)
                .build();
        log.info("Sending user subscription message through amqp");
        rabbitTemplate.convertSendAndReceive(MQConfig.USER_EXCHANGE,
                MQConfig.USER_ROUTING_KEY, new ChangeUserPremiumStatusRequest(userId,true));
        log.info("User subscription saving.....");
        userSubscriptionRepository.save(userSubscription);

    }

    @Override
    public void handleWebhookEvent(String payload, String sigHeader) throws StripeException {
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        switch (event.getType()){
            case "customer.subscription.deleted" : {
                log.info("Removing subscription...");
                Subscription subscription =  (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
                cancelUserSubscription(subscription);
                break;
            }
            case "customer.subscription.created" : {
                Subscription subscription =  (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
                createUserSubscription(subscription);
                break;
            }
            case "customer.subscription.update" : {
                Subscription subscription =  (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
                break;
            }
            default:
            {
                break;
            }
        }
    }

    @Override
    public List<UserSubscriptionDto> getUserSubscriptions(Long userId) {
        return userSubscriptionRepository.findByUserId(userId).stream().map(UserSubscriptionDto::new).toList();
    }

    @Override
    public List<GetRevenueResponse> getAllSubscriptions(String interval) {
        return userSubscriptionRepository.findAll().stream()
                .map(subscription -> {
                    LocalDate subscribedDate = subscription.getSubscribedAt().toLocalDate();
                    return GetRevenueResponse.builder()
                            .amount(subscription.getAmount())
                            .currency(subscription.getCurrency())
                            .subscribedAt(getFormattedDate(subscribedDate, interval))
                            .userId(subscription.getUserId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public GetTotalRevenueResponse getTotalRevenue() {
    List<UserSubscription> allSubscriptions = userSubscriptionRepository.findAll();
    Double totalAmount = Double.valueOf(allSubscriptions.stream().map(UserSubscription::getAmount).reduce(0F, Float::sum));
    return new GetTotalRevenueResponse(totalAmount);
    }

    @Override
    public Long getAllActiveSubscriptions() {
        return userSubscriptionRepository.countBySubscriptionCancelledFalse();
    }

    private String getFormattedDate(LocalDate subscribedDate, String interval) {
        DateTimeFormatter formatter = switch (interval) {
            case "yearly" -> DateTimeFormatter.ofPattern("yyyy");
            case "monthly" -> DateTimeFormatter.ofPattern("yyyy-MM");
            case "weekly" -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            default -> DateTimeFormatter.ISO_OFFSET_DATE;
        };
        return subscribedDate.format(formatter);
    }
}
