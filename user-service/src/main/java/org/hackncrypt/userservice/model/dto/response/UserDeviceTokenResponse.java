package org.hackncrypt.userservice.model.dto.response;

import jdk.jfr.SettingDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceTokenResponse {
    private String userDeviceToken;
}
