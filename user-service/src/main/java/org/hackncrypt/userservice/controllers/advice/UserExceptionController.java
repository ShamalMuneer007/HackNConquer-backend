package org.hackncrypt.userservice.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.model.dtos.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class UserExceptionController {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiError> handleInvalidUserInputException(InvalidInputException ex, WebRequest request){
        log.error(ex.getMessage());
        ApiError apiError = new ApiError(ex.getMessage(), LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),request.getDescription(false));
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }
}
