package org.hackncrypt.problemservice.exceptions.business;

public class DuplicateValueException extends RuntimeException {
    public DuplicateValueException(String message) {
        super(message);
    }
}
