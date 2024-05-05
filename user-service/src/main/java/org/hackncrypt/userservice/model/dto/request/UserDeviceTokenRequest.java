package org.hackncrypt.userservice.model.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDeviceTokenRequest {
    private Long userId;
    private String deviceToken;
}
