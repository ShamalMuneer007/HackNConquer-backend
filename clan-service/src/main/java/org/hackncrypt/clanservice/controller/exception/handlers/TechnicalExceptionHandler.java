package org.hackncrypt.clanservice.controller.exception.handlers;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.model.dto.error.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDate;

@RestControllerAdvice
@Slf4j
public class TechnicalExceptionHandler {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleIOException(IOException ex, WebRequest webRequest) {
        log.error("IO EXCEPTION OCCURRED : {}", ex.getMessage());
        org.hackncrypt.clanservice.model.dto.error.ApiError apiError = new org.hackncrypt.clanservice.model.dto.error.ApiError(
                "IO EXCEPTION: \n"+ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(SdkClientException .class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleAmazonSDKClientException(SdkClientException  ex, WebRequest webRequest) {
        log.error("AWS SDK CLIENT EXCEPTION OCCURRED : {}", ex.getMessage());
        org.hackncrypt.clanservice.model.dto.error.ApiError apiError = new org.hackncrypt.clanservice.model.dto.error.ApiError(
                "AWS SDK CLIENT EXCEPTION: \n"+ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(AmazonS3Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleAmazonS3Exception(AmazonS3Exception ex, WebRequest webRequest) {
        log.error("AWS EXCEPTION OCCURRED : {}", ex.getMessage());
        ApiError apiError = new ApiError(
                "AWS EXCEPTION: \n"+ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
