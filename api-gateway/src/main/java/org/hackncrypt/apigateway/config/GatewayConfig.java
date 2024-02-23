package org.hackncrypt.apigateway.config;

import org.hackncrypt.apigateway.filters.GatewayUriPathLogger;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public GlobalFilter globalFilter(){
        return new GatewayUriPathLogger();
    }
}
