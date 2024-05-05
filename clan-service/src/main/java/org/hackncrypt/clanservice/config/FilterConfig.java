package org.hackncrypt.discussionservice.config;

import org.hackncrypt.discussionservice.filters.AuthFilter;
import org.hackncrypt.discussionservice.util.JwtUtil;
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
