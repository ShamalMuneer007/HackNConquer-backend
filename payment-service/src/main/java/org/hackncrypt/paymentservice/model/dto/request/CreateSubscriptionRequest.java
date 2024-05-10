package org.hackncrypt.paymentservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateSubscriptionRequest {
    private String name;
    private Long userId;
    private String email;
    private String paymentMethod;
    private String priceId;
}
