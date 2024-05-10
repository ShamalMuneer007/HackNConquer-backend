package org.hackncrypt.paymentservice.controller.exception.handlers;

import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.model.error.ApiError;
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
public class TechnicalExceptionHandler {
    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public ResponseEntity<ApiError> handleError(StripeException ex, WebRequest webRequest) {
        log.error(ex.getMessage()+"\n"+ Arrays.toString(ex.getStackTrace()));
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.FAILED_DEPENDENCY.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
}
