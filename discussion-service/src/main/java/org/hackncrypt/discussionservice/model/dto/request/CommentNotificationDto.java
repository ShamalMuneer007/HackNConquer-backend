package org.hackncrypt.discussionservice.model.dto.request;

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
    private String commentedAt;
    private String toUsername;
    private String commentedUsername;
}
