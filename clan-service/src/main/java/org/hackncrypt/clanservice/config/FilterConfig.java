package org.hackncrypt.clanservice.config;

import org.hackncrypt.clanservice.filters.AuthFilter;
import org.hackncrypt.clanservice.util.JwtUtil;
import org.hackncrypt.clanservice.filters.AuthFilter;
import org.hackncrypt.clanservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class FilterConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public JwtUtil jwtUtil(@Value("${jwt.jwtSecret}") String jwtSecret){
        return new JwtUtil(jwtSecret);
    }
    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
    @Bean
    public FilterRegistrationBean<AuthFilter> loggingFilter(AuthFilter authFilter){
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
