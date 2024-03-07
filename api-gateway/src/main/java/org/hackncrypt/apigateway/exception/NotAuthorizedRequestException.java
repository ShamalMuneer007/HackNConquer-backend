package org.hackncrypt.apigateway.exception;

public class NotAuthorizedRequestException extends RuntimeException {
    public NotAuthorizedRequestException(String message) {
        super(message);
    }
}
