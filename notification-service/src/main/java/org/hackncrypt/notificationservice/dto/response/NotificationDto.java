package org.hackncrypt.notificationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.notificationservice.entity.Notification;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto implements Comparable<NotificationDto>{
    private Long notificationId;
    private Long userId;
    private String fromUsername;
    private String title;
    private String body;
    private boolean seen;
    private String notificationSendAt;
    public NotificationDto(Notification notification){
        this.notificationId = notification.getNotificationId();
        this.notificationSendAt = notification.getNotificationSendAt().format(DateTimeFormatter.ISO_DATE_TIME);
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.fromUsername = notification.getFromUsername();
    }

    @Override
    public int compareTo(NotificationDto other) {
        return this.getNotificationSendAt().compareTo(other.getNotificationSendAt());
    }
}
