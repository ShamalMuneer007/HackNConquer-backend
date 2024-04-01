package org.hackncrypt.testservice.controllers.advice;

import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.hackncrypt.testservice.exceptions.judge0.SandboxError;
import org.hackncrypt.testservice.exceptions.technical.InternalServerException;
import org.hackncrypt.testservice.models.dto.error.ApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TechnicalExceptionController {
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiError> handleInternalServerException(InternalServerException ex,WebRequest request){
        log.error("Something went wrong {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        return ResponseEntity.internalServerError().body(apiError);
    }
    @ExceptionHandler(SandboxError.class)
    public ResponseEntity<ApiError> handleSandboxError(SandboxError ex, WebRequest request){
        log.error("Something went wrong in Judge0 sandbox {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        return ResponseEntity.internalServerError().body(apiError);
    }
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleDataAccessException(DataAccessException ex, WebRequest request){
        log.error("An error occurred while accessing the database: {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        return ResponseEntity.internalServerError().body(apiError);
    }
    @ExceptionHandler(MongoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleMongoException(MongoException ex, WebRequest request) {
        log.error("An error occurred while accessing MongoDB: {}", ex.getMessage(), ex);

        String errorMessage = "An error occurred while accessing MongoDB";
        int errorCode = ex.getCode();

        ApiError apiError = new ApiError(
                errorMessage + " [Error Code: " + errorCode + "]",
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );

        return ResponseEntity.internalServerError().body(apiError);
    }
}
