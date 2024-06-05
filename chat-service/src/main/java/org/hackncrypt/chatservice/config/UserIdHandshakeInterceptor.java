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

import java.util.Arrays;
import java.util.Base64;
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
        log.info("USER TOKEN = {}",userToken);
        if(userToken != null){
            String userId = jwtUtil.getUserIdFromToken(userToken);
            log.info("Request header userId {} ",userId);
            if (userId != null) {
                attributes.put("userId", userId);
            }
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
    private String extractUserToken(HttpHeaders headers) {
        String cookie = headers.getFirst(HttpHeaders.COOKIE);
        log.info("REQUEST COOKIE  : {}",cookie);
        if (cookie != null) {
            String[] cookies = cookie.split("; ");
            for (String c : cookies) {
                if (c.startsWith("userToken=")) {
                    byte[] bytearray = Base64.getDecoder().decode(c.substring("userToken=".length()));
                    return new String(bytearray);
                }
            }
        }
        return null;
    }
}
