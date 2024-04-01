package org.hackncrypt.submissionservice.exceptions.judge0;

public class SandboxError extends RuntimeException {
    public SandboxError(Exception e, String message) {
        super(message,e);
    }

    public SandboxError(Exception e) {
        super(e);
    }
}
