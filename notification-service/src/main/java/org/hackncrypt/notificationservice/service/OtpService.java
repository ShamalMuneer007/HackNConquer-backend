package org.hackncrypt.notificationservice.service;

import org.hackncrypt.notificationservice.dto.OtpDto;

public interface OtpService {
    Boolean validateOtp(OtpDto otpDto);
}
