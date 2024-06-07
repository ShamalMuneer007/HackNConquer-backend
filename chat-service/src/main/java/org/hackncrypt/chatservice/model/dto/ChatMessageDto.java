package org.hackncrypt.chatservice.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hackncrypt.chatservice.entity.ChatMessage;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageDto {
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    private Long messageId;
    private Long replyFor;

    public ChatMessageDto(ChatMessage chat) {
        this.senderId = chat.getSenderId();
        this.messageId = chat.getId();
        this.receiverId = chat.getReceiverId();
        this.content = chat.getContent();
        this.timestamp = chat.getTimestamp();
        this.replyFor = chat.getReplyFor();
        this.read = chat.isRead();
    }
}
