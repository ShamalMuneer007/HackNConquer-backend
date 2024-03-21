package org.hackncrypt.userservice.proxies.feign;

import org.hackncrypt.userservice.model.dto.auth.OtpDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification",path = "/notification")
public interface NotificationFeignProxy {
    @PostMapping("/api/v1/auth/otp/verify-otp")
    ResponseEntity<Boolean> validateOtp(@RequestBody OtpDto otpDto);
}
