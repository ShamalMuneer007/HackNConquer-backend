package org.hackncrypt.paymentservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubscriptionResponse {
    private String clientSecret;
    private String customerId;
    private String subscriptionId;
}
