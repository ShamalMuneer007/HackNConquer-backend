package org.hackncrypt.notificationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentNotificationDto {
    private String toUserId;
    private String commentedUserId;
    private String comment;
    private String commentedUsername;
    private String toUsername;
    private String commentedAt;
}