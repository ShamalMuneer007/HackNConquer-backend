package org.hackncrypt.problemservice.interceptors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.annotations.Authorized;
import org.hackncrypt.problemservice.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("||||||||| Intercepting request ||||||||");
        if (handler instanceof HandlerMethod handlerMethod) {
            log.info("Getting the bean type of where the request is headed to");
            //Checks if the class is annotated with @Authorized
            Class<?> handlerClass = handlerMethod.getBeanType();//Returns type of the handlerMethod
            log.info("Bean type : {}",handlerClass.getClasses().getClass().getName());
            if (handlerClass.isAnnotationPresent(Authorized.class)) {
                Authorized classRoleAuthorization = handlerClass.getAnnotation(Authorized.class);
                // Get roles from the class-level annotation
                String[] classAllowedRoles = classRoleAuthorization.value();
                String userRole = JwtUtil.getRoleFromToken(JwtUtil.getJWTFromRequest(request));
                // Check if the user has the required role
                if (!Arrays.asList(classAllowedRoles).contains(userRole)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient privileges");
                    return false;
                }
            }
            // Check if the method is annotated with @Authorized
            if (handlerMethod.getMethod().isAnnotationPresent(Authorized.class)) {
                Authorized roleAuthorization = handlerMethod.getMethodAnnotation(Authorized.class);

                // Get roles from the annotation
                String[] allowedRoles = roleAuthorization.value();
                log.info("Allowed Role for the method {}", allowedRoles[0]);
                String userRole = JwtUtil.getRoleFromToken(request.getHeader("Authorization"));
                // Check if the user has the required role
                if (!Arrays.asList(allowedRoles).contains(userRole)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient privileges");
                    return false;
                }
            }
        }
        return true;
    }
}
