package org.hackncrypt.apigateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndPoints = List.of(
            "/user/api/v1/auth/register",
            "/user/api/v1/auth/login",
            "/user/api/v1/auth/google/oauth/login",
            "/eureka"
    );
    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndPoints.stream().noneMatch(url ->
                    serverHttpRequest.getURI().getPath().contains(url));
}
