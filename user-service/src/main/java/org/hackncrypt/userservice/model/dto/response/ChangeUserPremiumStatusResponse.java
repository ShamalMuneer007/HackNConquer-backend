package org.hackncrypt.userservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeUserPremiumStatusResponse {
    private Long userId;
    private Boolean status;
}
