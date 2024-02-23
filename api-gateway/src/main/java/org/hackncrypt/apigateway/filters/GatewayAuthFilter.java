package org.hackncrypt.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.apigateway.util.JwtUtil;
import org.hackncrypt.apigateway.util.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class GatewayAuthFilter extends AbstractGatewayFilterFactory<GatewayAuthFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    JwtUtil jwtUtil;

    public GatewayAuthFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("Inside auth filter");
            ServerHttpRequest request = exchange.getRequest();
            if(routeValidator.isSecured.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    log.warn("No AUTHORIZATION header present in request header");
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
                String token;
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if(Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")){
                    token = authHeader.substring(7);
                    try{
                        log.info("validation token .... ");
                        if(jwtUtil.validateToken(token)) {
                            request = exchange
                                    .getRequest()
                                    .mutate()
                                    .header("token", token)
                                    .header("username", jwtUtil.getUsernameFromToken(token))
                                    .build();
                        }
                        else{
                            log.error("Invalid token....");
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    }
                    catch(Exception e){
                        log.error("Something went wrong in authorization filter");
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }
            }
            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    public static class Config{

    }
}
