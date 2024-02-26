package org.hackncrypt.notificationservice.controller;

import org.hackncrypt.notificationservice.dto.OtpDto;
import org.hackncrypt.notificationservice.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class OtpController {
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/otp/verify-otp")
    ResponseEntity<Boolean> verifyOtp(@RequestBody OtpDto otpDto){
        try{
            return ResponseEntity.ok(otpService.validateOtp(otpDto));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
