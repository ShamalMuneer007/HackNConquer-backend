package org.hackncrypt.notificationservice.service;

import org.hackncrypt.notificationservice.dto.response.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getUserNotifications(Long userId);
}
