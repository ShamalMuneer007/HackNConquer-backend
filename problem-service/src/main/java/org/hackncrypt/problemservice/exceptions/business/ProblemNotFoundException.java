package org.hackncrypt.problemservice.exceptions.business;

public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}
