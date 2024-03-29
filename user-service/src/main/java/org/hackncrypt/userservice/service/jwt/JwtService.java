package org.hackncrypt.userservice.service.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.JwtGenerationException;
import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtService {
    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.jwtExpiration}")
    private Long jwtExpiration;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        log.info("username : {}",username);
        Date currentDate = new Date();
        try {
            User user = userRepository.findByUsername(username);
            Date expirationTime = new Date(currentDate.getTime() + jwtExpiration);
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return Jwts.builder()
                    .setSubject(username)
                    .claim("role", roles.get(0))
                    .claim("userId", Long.toString(user.getUserId()))
                    .claim("profileImage",user.getProfileImageUrl())
                    .setIssuedAt(currentDate)
                    .setExpiration(expirationTime)
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
        }
        catch (Exception e){
            log.error("Something went wrong while generating token : {}",e.getMessage());
            throw new JwtGenerationException(e.getMessage());
        }
    }
        public String getUsernameFromToken(String token){
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return  claims.getSubject();
        }
        public boolean validateToken(String token){
        log.info("Validating token ....");
            try {
                Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
                return true;
            }
            catch (ExpiredJwtException e){
                throw new ExpiredJwtException(e.getHeader(),e.getClaims(),"Jwt token is expired");
            }
            catch (InvalidClaimException e){
                throw new JwtException("Jwt token is invalid");
            }
            catch (Exception e){
                throw new RuntimeException("Something went wrong with the jwt token validation");
            }

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
