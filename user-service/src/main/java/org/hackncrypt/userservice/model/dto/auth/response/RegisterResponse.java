package org.hackncrypt.userservice.model.dto.auth.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String accessToken;
}
