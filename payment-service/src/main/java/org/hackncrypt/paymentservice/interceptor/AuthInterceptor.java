package org.hackncrypt.paymentservice.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.annotations.Authorized;
import org.hackncrypt.paymentservice.util.JwtUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {

            Class<?> handlerClass = handlerMethod.getBeanType();
            log.info(handlerClass.getName());//Returns type of the handlerMethod

            //Checks if the class is annotated with @Authorized
            if (handlerClass.isAnnotationPresent(Authorized.class)) {
                log.info("Validating Authorization");
                Authorized classRoleAuthorization = handlerClass.getAnnotation(Authorized.class);

                // Get roles from the class-level annotation
                String[] classAllowedRoles = classRoleAuthorization.value();
                log.info(Arrays.asList(classAllowedRoles).toString());
                String userRole = JwtUtil.getRoleFromToken(JwtUtil.getJWTFromRequest(request));
                log.info(userRole);

                // Checks if the user has the required role
                if (!Arrays.asList(classAllowedRoles).contains(userRole)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient privileges");
                    return false;
                }
            }
            // Checks if the method is annotated with @Authorized
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
