package org.hackncrypt.testservice.exceptions.technical;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message, Exception e) {
        super(message,e);
    }
}
