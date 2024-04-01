package org.hackncrypt.testservice.exceptions.judge0;

public class SandboxStandardError extends RuntimeException {
    public SandboxStandardError(String message,Exception e) {
        super(message,e);
    }
    public SandboxStandardError(String message) {
        super(message);
    }
}
