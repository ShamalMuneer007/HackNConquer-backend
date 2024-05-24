package org.hackncrypt.notificationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification implements Comparable<Notification> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notificationId;
    private Long userId;
    private String fromUsername;
    private String title;
    private String body;
    private boolean seen;
    @CreationTimestamp
    private LocalDateTime notificationSendAt;

    @Override
    public int compareTo(Notification other) {
        return this.getNotificationSendAt().compareTo(other.getNotificationSendAt());
    }
}
