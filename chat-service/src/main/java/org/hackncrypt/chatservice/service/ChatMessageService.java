package org.hackncrypt.chatservice.service;

import org.hackncrypt.chatservice.model.dto.ChatMessageDto;
import org.hackncrypt.chatservice.model.dto.UnreadMessageDto;

import java.util.List;

public interface ChatMessageService {
    void sendMessage(ChatMessageDto message);

    List<ChatMessageDto> getMessages(Long userId1, Long userId2);

    void readAllMessages(Long receiverId, Long senderId);

    List<UnreadMessageDto> getUnreadMessagesFromSenders(List<Long> senders, Long receiverId);
}
