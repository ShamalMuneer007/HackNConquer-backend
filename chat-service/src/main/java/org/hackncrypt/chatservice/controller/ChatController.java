package org.hackncrypt.chatservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.chatservice.model.dto.ChatMessageDto;
import org.hackncrypt.chatservice.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatMessageService chatMessageService;
    @MessageMapping("/message")
    public void sendMessage(@Payload ChatMessageDto message)  {
        log.info("SEND MESSAGE PAYLOAD {}", message);
        chatMessageService.sendMessage(message);
    }
    @MessageMapping("/message/{receiverId}/read/{senderId}")
    public void readMessage(@DestinationVariable("senderId") Long senderId,@DestinationVariable("receiverId") Long receiverId){
        log.info("Receiver : {} read messages send by user : {}",receiverId,senderId);
        chatMessageService.readAllMessages(receiverId,senderId);
    }
}