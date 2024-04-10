package org.hackncrypt.userservice.model.dto.auth.response;

import lombok.*;
import org.hackncrypt.userservice.model.dto.UserDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserDto userInfo;
    private String message;
}
