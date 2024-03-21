package org.hackncrypt.problemservice.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

/*
    This filter authorizes the incoming request to the service by verifying the token and this filter also
    does role based authorization (ADMIN API endpoints starts with "/api/v1/admin") for APIs
 */
@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Value("${jwt.jwtSecret}")
    private String jwtSecret;
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        log.info("request URI : {}", request.getRequestURI());
        if (request.getRequestURI().startsWith("/problem/actuator") ||
                request.getRequestURI().startsWith("/problem/eureka") ||
                request.getRequestURI().startsWith("/problem/swagger-ui")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token;
        try {
            JwtUtil.init(jwtSecret);
            token = JwtUtil.getJWTFromRequest(request);
            if (!JwtUtil.validateToken(token)) {
                log.error("Invalid JWT token");
                setUnauthorizedResponse(response, "Invalid Token");
                return;
            }
            log.info("validated jwt token");
            if (Objects.isNull(token)) {
                log.error("Unauthorized request");
                setUnauthorizedResponse(response, "Unauthorized request");
                return;
            }
            log.info("extracting role from token ...");
            String role = JwtUtil.getRoleFromToken(token);
            log.info("role from jwt token : {}", role);
        }
        catch (SignatureException e) {
            log.error("JWT token has been tampered with");
            setUnauthorizedResponse(response, "Tampered JWT token");
            return;
        }
        catch (ExpiredJwtException e) {
            log.error("Expired jwt");
            setUnauthorizedResponse(response, "Expired JWT");
            return;
        } catch (Exception e) {
            log.error("Something went wrong while parsing the token {}", e.getMessage());
            setUnauthorizedResponse(response, "Error parsing JWT token");
            return;
        }

        request.setAttribute("userId", JwtUtil.getUserIdFromToken(token));
        filterChain.doFilter(request, response);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
        response.getWriter().flush();
    }
}