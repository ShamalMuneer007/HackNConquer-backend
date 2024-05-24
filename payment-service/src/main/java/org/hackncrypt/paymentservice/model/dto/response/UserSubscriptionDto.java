package org.hackncrypt.paymentservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hackncrypt.paymentservice.model.entity.UserSubscription;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscriptionDto {
    private Float amount;
    private String currency;
    private String subscribedAt;
    private boolean isSubscriptionCancelled;
    private String subscriptionCancelledAt;
    public UserSubscriptionDto(UserSubscription subscription){
        this.amount = subscription.getAmount();
        this.currency = subscription.getCurrency();
        this.isSubscriptionCancelled = subscription.isSubscriptionCancelled();
        this.subscribedAt = subscription.getSubscribedAt() != null ?
                subscription.getSubscribedAt().format(DateTimeFormatter.ISO_DATE_TIME) : null;
        this.subscriptionCancelledAt = subscription.getSubscriptionCancelledAt() != null ?
                subscription.getSubscriptionCancelledAt().format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}
