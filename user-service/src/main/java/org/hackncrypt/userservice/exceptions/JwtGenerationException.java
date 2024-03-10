package org.hackncrypt.userservice.exceptions;

public class JwtGenerationException extends RuntimeException {
    public JwtGenerationException(String message) {
        super(message);
    }
    public JwtGenerationException(String message,Exception e) {
        super(message,e);
    }
}
