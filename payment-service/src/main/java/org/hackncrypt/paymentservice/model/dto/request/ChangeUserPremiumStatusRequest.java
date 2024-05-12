package org.hackncrypt.paymentservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserPremiumStatusRequest {
    private Long userId;
    private Boolean status;
}
