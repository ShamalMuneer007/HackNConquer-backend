package org.hackncrypt.notificationservice.integrations;

import org.hackncrypt.notificationservice.dto.UserDeviceTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
@FeignClient(name = "users", path = "/user/api/v1")
public interface UserFeignProxy {
    @GetMapping("/get-device-token/{userId}")
    ResponseEntity<UserDeviceTokenResponse> getUserDeviceToken(@PathVariable Long userId);
}
