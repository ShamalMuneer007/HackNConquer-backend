package org.hackncrypt.problemservice.exceptions.business;

public class NoSuchValueException extends RuntimeException {
    public NoSuchValueException(String message) {
        super(message);
    }
}
