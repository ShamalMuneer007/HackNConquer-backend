package org.hackncrypt.paymentservice.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeCustomerDetail{
    private long userId;
    private String customerId;
    private String subscriptionId;
    private LocalDateTime subscriptionEndDate;
    private LocalDateTime subscriptionStartDate;
    private String subscriptionPlan;
}
