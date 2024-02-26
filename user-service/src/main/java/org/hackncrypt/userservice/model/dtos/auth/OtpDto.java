package org.hackncrypt.userservice.model.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpDto {
    @JsonProperty("email")
    private String email;
    @JsonProperty("otp")
    private Integer otp;
}
