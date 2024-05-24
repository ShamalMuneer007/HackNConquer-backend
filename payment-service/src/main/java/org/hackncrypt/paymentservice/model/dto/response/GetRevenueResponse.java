package org.hackncrypt.paymentservice.model.dto.response;

import lombok.*;
import org.hackncrypt.paymentservice.model.entity.UserSubscription;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRevenueResponse {
    private Float amount;
    private String currency;
    private String subscribedAt;
    private Long userId;


    public GetRevenueResponse(UserSubscription subscription) {
        this.amount = subscription.getAmount();
        this.currency = subscription.getCurrency();
        this.subscribedAt = subscription.getSubscribedAt().format(DateTimeFormatter.ISO_OFFSET_DATE);
        this.userId = subscription.getUserId();
    }
}
