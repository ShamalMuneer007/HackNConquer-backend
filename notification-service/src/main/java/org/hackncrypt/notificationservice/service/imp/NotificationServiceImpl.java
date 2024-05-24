package org.hackncrypt.notificationservice.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.notificationservice.dto.response.NotificationDto;
import org.hackncrypt.notificationservice.entity.Notification;
import org.hackncrypt.notificationservice.repository.NotificationRepository;
import org.hackncrypt.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .sorted().map(NotificationDto::new)
                .sorted(Comparator.comparing(NotificationDto::getNotificationSendAt, Comparator.reverseOrder()))
                .toList();
    }
}
