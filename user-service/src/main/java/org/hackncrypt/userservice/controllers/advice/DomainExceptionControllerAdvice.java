package org.hackncrypt.userservice.controllers.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.exceptions.NoSuchValueException;
import org.hackncrypt.userservice.exceptions.business.FriendRequestException;
import org.hackncrypt.userservice.exceptions.business.UserNotFoundException;
import org.hackncrypt.userservice.model.dto.response.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.Arrays;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class DomainExceptionControllerAdvice {
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiError> handleInvalidUserInputException(InvalidInputException ex, WebRequest request){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(ex.getMessage(), LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),request.getDescription(false), Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNoUserNotFoundException(UserNotFoundException ex, WebRequest request){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(ex.getMessage(), LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),request.getDescription(false), Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(FriendRequestException.class)
    public ResponseEntity<ApiError> handleNoUserNotFoundException(FriendRequestException ex, WebRequest request){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(ex.getMessage(), LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),request.getDescription(false), Arrays.toString(ex.getStackTrace()));
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
                webRequest.getDescription(false),
                Arrays.toString(ex.getStackTrace())
        );
        return ResponseEntity.badRequest().body(apiError);
    }

}
