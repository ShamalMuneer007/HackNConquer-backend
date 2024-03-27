package org.hackncrypt.apigateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
@Slf4j
public class RouteValidator {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    public static final List<String> securedApiEndPoints = List.of(
            "/api/v1/user/**",
            "/api/v1/admin/**"
    );
    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> {
                String path = serverHttpRequest.getURI().getPath();
                int contextPathEndIndex = path.indexOf('/', 1);
                String pathWithoutContextPath = (contextPathEndIndex != -1) ? path.substring(contextPathEndIndex) : path;
                log.info("REQUEST URI WITHOUT CONTEXT PATH : {}",pathWithoutContextPath);
                return securedApiEndPoints.stream()
                        .anyMatch(pattern -> antPathMatcher.matchStart(pattern, pathWithoutContextPath));
            };
}
