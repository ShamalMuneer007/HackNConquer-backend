package org.hackncrypt.notificationservice.repository;

import org.hackncrypt.notificationservice.dto.response.NotificationDto;
import org.hackncrypt.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserId(Long userId);
}
