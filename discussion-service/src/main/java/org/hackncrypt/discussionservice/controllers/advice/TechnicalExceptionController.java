package org.hackncrypt.discussionservice.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.discussionservice.exceptions.firebase.FMCQueueException;
import org.hackncrypt.discussionservice.model.dto.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class TechnicalExceptionController {
    @ExceptionHandler(FMCQueueException.class)
    public ResponseEntity<ApiError> handleFCMQueueException(FMCQueueException exception, WebRequest webRequest){
        log.error("Something went wrong while publishing message in FCM amqp queue: {}", exception.getMessage());
        log.error(exception.getMessage()+"\n"+ Arrays.toString(exception.getStackTrace()));
        ApiError apiError = new ApiError(
                "Something went wrong while publishing message in FCM amqp queue violation : \n"+exception.getMessage(),
                LocalDate.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                webRequest.getDescription(false)
        );
        return ResponseEntity.internalServerError().body(apiError);
    }
}
