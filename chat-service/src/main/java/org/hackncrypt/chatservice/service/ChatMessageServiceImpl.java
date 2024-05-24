package org.hackncrypt.chatservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.chatservice.entity.ChatMessage;
import org.hackncrypt.chatservice.model.dto.ChatMessageDto;
import org.hackncrypt.chatservice.model.dto.UnreadMessageDto;
import org.hackncrypt.chatservice.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now());
        ChatMessage chat = chatMessageRepository.save(new ChatMessage(message));
        log.info("Sending message {} to user : {}",chat,message.getReceiverId());
        try {
            messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()),"/queue/messages", new ChatMessageDto(chat));
            messagingTemplate.convertAndSendToUser(String.valueOf(message.getSenderId()),"/queue/messages", new ChatMessageDto(chat));
            messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()),"/queue/unread/messages", message.getSenderId());
        }
        catch (Exception e){
            log.error("ERROR , {}",e.getMessage());
        }
    }

    @Override
    public List<ChatMessageDto> getMessages(Long userId1, Long userId2) {
        log.info("Getting messages for sender : {}, Receiver : {}", userId1, userId2);
        List<ChatMessage> messages = chatMessageRepository.findUserMessages(userId1, userId2);
        log.info("MESSAGES >>> {}",messages);
        return messages.stream().map(ChatMessageDto::new).toList();
    }

    @Override
    public void readAllMessages(Long receiverId, Long senderId) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId,receiverId);
        messages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(messages);
        messagingTemplate.convertAndSend("/queue/messages/read/"+ senderId, true);
    }

    @Override
    public List<UnreadMessageDto> getUnreadMessagesFromSenders(List<Long> senders, Long receiverId) {
        return chatMessageRepository.findCountOfUnreadMessagesGroupedBySender(senders,receiverId);
    }
}
