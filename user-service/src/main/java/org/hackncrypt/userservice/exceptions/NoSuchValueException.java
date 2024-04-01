package org.hackncrypt.userservice.exceptions;

public class NoSuchValueException extends RuntimeException {
    public NoSuchValueException(String message) {
        super(message);
    }
}
