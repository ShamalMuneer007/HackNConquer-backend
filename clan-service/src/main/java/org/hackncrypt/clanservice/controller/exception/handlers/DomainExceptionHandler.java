package org.hackncrypt.clanservice.controller.exception.handlers;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.exception.InvalidInputException;
import org.hackncrypt.clanservice.exception.NoSuchValueException;
import org.hackncrypt.clanservice.exception.business.ClanException;
import org.hackncrypt.clanservice.exception.business.ClanNotFoundException;
import org.hackncrypt.clanservice.model.dto.error.ApiError;
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
public class DomainExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleInvalidUserInputException(InvalidInputException ex, WebRequest webRequest){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(ClanException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleClanException(ClanException ex, WebRequest webRequest){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(ClanNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleClanNotFoundException(ClanNotFoundException ex, WebRequest webRequest){
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(NoSuchValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleNoSuchValueException(NoSuchValueException ex, WebRequest webRequest){
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
