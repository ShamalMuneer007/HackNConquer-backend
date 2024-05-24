package org.hackncrypt.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.notificationservice.dto.response.NotificationDto;
import org.hackncrypt.notificationservice.service.NotificationService;
import org.hackncrypt.notificationservice.service.PushNotificationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserNotificationController {
    private final NotificationService notificationService;
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId){
       return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
}
