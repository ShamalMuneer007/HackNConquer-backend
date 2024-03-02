package org.hackncrypt.problemservice.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
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
        log.info("request URI : {}",request.getRequestURI());
        if (request.getRequestURI().startsWith("/cart/actuator") ||
                request.getRequestURI().startsWith("/cart/eureka") ||
                request.getRequestURI().startsWith("/cart/swagger-ui")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        String token;
        try {
            /*Init is a static method of JwtUtil custom package
             where jwtSecret is initialized in  Util class for validation process to work */
           JwtUtil.init(jwtSecret);
            token = JwtUtil.getJWTFromRequest(request);
            if (!JwtUtil.validateToken(token)) {
                log.error("Invalid JWT token");
                throw new AccessDeniedException("Invalid Token");
            }
            log.info("validated jwt token");
            if (Objects.isNull(token)) {
                log.error("Unauthorized request");
                throw new RuntimeException("Unauthorized request");
            }
            log.info("extracting role from token ...");
            String role = JwtUtil.getRoleFromToken(token);
            log.info("role from jwt token : {}",role);
            //Role based authorization is done here
//            if (request.getRequestURI().startsWith("/cart/api/v1/admin")) {
//                log.info("Verifying role.... ");
//                if (!JwtUtil.getRoleFromToken(token).equals("ROLE_ADMIN")) {
//                    log.error("Unauthorized request for admin api");
//                    throw new AccessDeniedException("Unauthorized request");
//                }
//            }
        } catch (ExpiredJwtException e) {
            log.error("Expired jwt");
            throw new ServletException("Expired Jwt");
        } catch (InvalidClaimException e) {
            log.error("Tampered Jwt token");
            throw new AccessDeniedException("Tampered Jwt token");
        } catch (Exception e) {
            log.error("Something went wrong while parsing the token {}",e.getMessage());
            throw new AccessDeniedException("Something went wrong while parsing the jwt token");
        }
        request.setAttribute("userId",JwtUtil.getUserIdFromToken(token));
        filterChain.doFilter(request, response);
    }
}