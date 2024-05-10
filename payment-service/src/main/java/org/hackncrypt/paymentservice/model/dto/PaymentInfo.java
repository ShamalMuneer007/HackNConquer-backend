package org.hackncrypt.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfo {
    private String source;
    private String amount;
    private String currency;
    private String description;
}
