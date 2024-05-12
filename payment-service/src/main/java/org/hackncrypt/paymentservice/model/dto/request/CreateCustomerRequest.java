package org.hackncrypt.paymentservice.model.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateCustomerRequest {
    private String name;
    private Long userId;
    private String email;
    private String paymentMethod;
    private String priceId;
}
