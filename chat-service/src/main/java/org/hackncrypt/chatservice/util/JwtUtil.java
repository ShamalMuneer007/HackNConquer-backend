package org.hackncrypt.chatservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.jwtSecret}")
    private String jwtSecret;
    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return  claims.getSubject();
    }
    public String getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        String userId = (String) claims.get("userId");
        log.info("UserId FROM JWT : {}",userId);
        return userId;
    }

    public void expireToken(String token){
        Date currentDate = new Date();
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        claims.setExpiration(new Date(currentDate.getTime()));
    }
    public String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}