package org.hackncrypt.paymentservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CreateCustomerResponse {
    private String clientSecret;
    private String customerId;
}
