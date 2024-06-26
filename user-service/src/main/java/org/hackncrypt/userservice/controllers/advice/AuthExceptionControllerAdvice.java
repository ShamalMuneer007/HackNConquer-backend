package org.hackncrypt.userservice.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.JwtGenerationException;
import org.hackncrypt.userservice.exceptions.UserAuthenticationException;
import org.hackncrypt.userservice.model.dto.response.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.Arrays;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionControllerAdvice {
    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(UserAuthenticationException ex,
                                                                  WebRequest request){
        log.warn("wrong user credentials");

        ApiError apiError = new ApiError("Invalid username or password",LocalDate.now(),
                HttpStatus.UNAUTHORIZED.value(),request.getDescription(false),Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(JwtGenerationException.class)
    public ResponseEntity<ApiError> handleJwtGenerationException(JwtGenerationException ex,
                                                                 WebRequest webRequest){
        log.error("jwt token generation error : {}",ex.getMessage());
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError =
                new ApiError(ex.getMessage(),
                        LocalDate.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        webRequest.getDescription(false), Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
