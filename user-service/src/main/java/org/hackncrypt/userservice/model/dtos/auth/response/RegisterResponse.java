package org.hackncrypt.userservice.model.dtos.auth.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String message;
    private String token;
}
