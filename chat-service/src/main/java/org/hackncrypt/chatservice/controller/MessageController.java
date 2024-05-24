package org.hackncrypt.chatservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.chatservice.config.WebSocketEventListener;
import org.hackncrypt.chatservice.model.dto.ChatMessageDto;
import org.hackncrypt.chatservice.model.dto.UnreadMessageDto;
import org.hackncrypt.chatservice.service.ChatMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ChatMessageService chatMessageService;
    private final WebSocketEventListener webSocketEventListener;
    @GetMapping("/{userId1}/{userId2}")
    public List<ChatMessageDto> getMessages(@PathVariable Long userId1, @PathVariable Long userId2) {
        return chatMessageService.getMessages(userId1, userId2);
    }
    @PostMapping("/new/{receiverId}")
    public List<UnreadMessageDto> getUnreadMessages(@PathVariable Long receiverId, @RequestBody List<Long> senders){
        return chatMessageService.getUnreadMessagesFromSenders(senders,receiverId);
    }
    @GetMapping("/online-users")
    public Set<String> getAllOnlineUsers(){
        return webSocketEventListener.getAllOnlineUsers();
    }
}