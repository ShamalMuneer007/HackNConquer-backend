package org.hackncrypt.paymentservice.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.constants.Constants;
import org.hackncrypt.paymentservice.util.JwtUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/*
    This filter authorizes the incoming request to the service by verifying the token
 */
@Slf4j
@Setter
public class AuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        log.info("request URI : {}", request.getRequestURI());
        if (!isProtectedUri(request.getRequestURI())
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = JwtUtil.getJWTFromRequest(request);
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
            request.setAttribute("userId", JwtUtil.getUserIdFromToken(token));
            filterChain.doFilter(request, response);
        }
        catch (SignatureException e) {
            log.error("JWT token has been tampered with");
            setUnauthorizedResponse(response, "Tampered JWT token");
        }
        catch (ExpiredJwtException e) {
            log.error("Expired jwt");
            setUnauthorizedResponse(response, "Expired JWT");
        } catch (Exception e) {
            log.error("Something went wrong while parsing the token {}", e.getMessage());
            setUnauthorizedResponse(response, "Error parsing JWT token");
        }
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
        response.getWriter().flush();
    }
    private boolean isProtectedUri(String requestURI) {
        int contextPathEndIndex = requestURI.indexOf('/', 1);
        String pathWithoutContextPath = (contextPathEndIndex != -1) ? requestURI.substring(contextPathEndIndex) : requestURI;
        log.info("REQUEST URI WITHOUT CONTEXT PATH : {}",pathWithoutContextPath);
        return Constants.getProtectedURIPatterns().stream()
                .anyMatch(pattern ->{
                    log.info(pattern+" REQUEST : "+pathWithoutContextPath);
                    return antPathMatcher.match(pattern, pathWithoutContextPath);
                });
    }
}