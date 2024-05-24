package org.hackncrypt.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hackncrypt.chatservice.model.dto.ChatMessageDto;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable= false)
    private Long senderId;
    @Column(nullable= false)
    private Long receiverId;
    private String content;
    @Column(name = "message_read")
    private boolean read;
    private Long replyFor;
    private LocalDateTime timestamp;

    public ChatMessage(ChatMessageDto message) {
        this.content = message.getContent();
        this.receiverId = message.getReceiverId();
        this.senderId = message.getSenderId();
        this.timestamp = message.getTimestamp();
        this.read = message.isRead();
        this.replyFor = message.getReplyFor();
    }
}
