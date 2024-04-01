package org.hackncrypt.submissionservice.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.submissionservice.exceptions.judge0.ClientSandboxCodeExecutionError;
import org.hackncrypt.submissionservice.exceptions.judge0.SandboxCompileError;
import org.hackncrypt.submissionservice.exceptions.judge0.SandboxStandardError;
import org.hackncrypt.submissionservice.models.dto.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
@Slf4j
public class DomainExceptionController {
    @ExceptionHandler(ClientSandboxCodeExecutionError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleClientSandboxCodeExecutionError(ClientSandboxCodeExecutionError ex, WebRequest webRequest){
        log.warn(ex.getMessage(),ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }


    @ExceptionHandler(SandboxCompileError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleSandboxCompileError(SandboxCompileError ex, WebRequest webRequest){
        log.warn(ex.getMessage(),ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(SandboxStandardError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleSandboxStandardError(SandboxStandardError ex, WebRequest webRequest){
        log.warn(ex.getMessage(),ex);
        ApiError apiError = new ApiError(
                ex.getMessage(),
                LocalDate.now(),
                HttpStatus.BAD_REQUEST.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.badRequest().body(apiError);
    }

}
