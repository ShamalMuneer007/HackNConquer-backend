package org.hackncrypt.problemservice.exceptions.judge0;

public class SandboxError extends RuntimeException {
    public SandboxError(Exception e, String message) {
        super(message,e);
    }
    public SandboxError(String message) {
        super(message);
    }

    public SandboxError(Exception e) {
        super(e);
    }
}
