package org.hackncrypt.chatservice.config;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.chatservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class UserIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public UserIdHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("Before handshake processing Request {}....",request.getHeaders());
        String userToken = extractUserToken(request.getHeaders());
        String userId = jwtUtil.getUserIdFromToken(userToken);
        log.info("Request header userId {} ",userId);
        if (userId != null) {
            attributes.put("userId", userId);
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
    private String extractUserToken(HttpHeaders headers) {
        String cookie = headers.getFirst(HttpHeaders.COOKIE);
        if (cookie != null) {
            String[] cookies = cookie.split("; ");
            for (String c : cookies) {
                if (c.startsWith("userToken=")) {
                    return c.substring("userToken=".length());
                }
            }
        }
        return null;
    }
}
