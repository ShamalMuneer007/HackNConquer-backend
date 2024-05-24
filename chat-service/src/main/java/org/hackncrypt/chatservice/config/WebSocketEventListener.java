package org.hackncrypt.chatservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.chatservice.util.JwtUtil;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) throws InterruptedException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) accessor.getSessionAttributes().get("userId");
        log.info("CONNECTION ESTABLISHED : {}",userId);
        if (userId != null) {
            log.info("User ID: {}", userId);
            connectedUsers.add(userId);
            sendOnlineUsersToUser(userId);
            broadcastOnlineUsersCount();
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws InterruptedException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) accessor.getSessionAttributes().get("userId");
        log.info("USER DISCONNECTED {}",accessor.getSessionAttributes());
        log.info("SESSIONS : {}",accessor.getSessionAttributes());
        if (userId != null) {
            connectedUsers.remove(userId);
            log.info("User disconnected: {}", userId);
            broadcastOnlineUsersCount();
        }
    }


    private void broadcastOnlineUsersCount() throws InterruptedException {
        log.info("Connected Users : {}",connectedUsers);
        messagingTemplate.convertAndSend("/topic/onlineUsers", connectedUsers);
    }
    private void sendOnlineUsersToUser(String userId) throws InterruptedException {
        Thread.sleep(1500);
        log.info("Connected Users : {}",connectedUsers);
        messagingTemplate.convertAndSendToUser(userId,"/queue/onlineUsers", connectedUsers);
    }

    public Set<String> getAllOnlineUsers() {
        return connectedUsers;
    }
}