package org.hackncrypt.problemservice.exceptions;

public class SandboxStandardError extends RuntimeException {
    public SandboxStandardError(String message,Exception e) {
        super(message,e);
    }
    public SandboxStandardError(String message) {
        super(message);
    }
}
