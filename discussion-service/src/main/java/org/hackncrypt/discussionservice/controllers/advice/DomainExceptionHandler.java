package org.hackncrypt.discussionservice.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.discussionservice.exceptions.InvalidInputException;
import org.hackncrypt.discussionservice.exceptions.NoSuchValueException;
import org.hackncrypt.discussionservice.model.dto.error.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiError> handleInvalidUserInputException(InvalidInputException ex, WebRequest request){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(ex.getMessage(), LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),request.getDescription(false));
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoSuchValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleNoSuchValueException(NoSuchValueException ex,WebRequest webRequest){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
}
