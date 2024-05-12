package org.hackncrypt.paymentservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateSubscriptionRequest {
    private String priceId;
    private String customerId;
    private Long userId;
    private String email;
    private String name;
    private String paymentMethodId;
}
