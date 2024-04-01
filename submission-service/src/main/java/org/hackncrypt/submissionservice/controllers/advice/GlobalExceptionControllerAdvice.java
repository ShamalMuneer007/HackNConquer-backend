package org.hackncrypt.submissionservice.controllers.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.models.dto.error.ApiError;
import org.hackncrypt.submissionservice.models.dto.error.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            log.info(field);
            log.info(message);
            if (!errors.containsKey(field)) {
                errors.put(field, List.of(message));
            } else {
                errors.get(field).add(message);
            }
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .path(request.getDescription(true))
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDate.now())
                .message("Invalid request!!!").build();
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex, WebRequest webRequest){
        log.error("An error occurred: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeignException(FeignException ex, WebRequest webRequest) {
           try{
            ObjectMapper objectMapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            ApiError apiError = objectMapper.readValue(ex.contentUTF8(), ApiError.class);
            log.error("FEIGN ERROR : {}",apiError);
               return new ResponseEntity<>(apiError, HttpStatusCode.valueOf(ex.status()));

        } catch (JsonProcessingException e) {
            log.error("Error deserializing Feign exception: {}", e.getMessage());
            ApiError fallbackError = new ApiError("Error deserializing Feign exception", LocalDate.now(), ex.status(), webRequest.getDescription(false));
            return ResponseEntity.status(HttpStatusCode.valueOf(ex.status()))
                    .body(fallbackError);
        }
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest webRequest) {
        log.error("An error occurred: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}