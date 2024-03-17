package org.hackncrypt.problemservice.controllers.advice;


import lombok.extern.slf4j.Slf4j;

import org.hackncrypt.problemservice.exceptions.business.DuplicateValueException;
import org.hackncrypt.problemservice.exceptions.business.NoSuchValueException;
import org.hackncrypt.problemservice.model.dto.error.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class DomainExceptionControllerAdvice {
    @ExceptionHandler(NoSuchValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleNoSuchValueException(NoSuchValueException ex,WebRequest webRequest){
        log.warn(ex.getMessage());
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(DuplicateValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleDuplicateValueException(DuplicateValueException ex, WebRequest webRequest){
        log.warn(ex.getMessage());
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
}
