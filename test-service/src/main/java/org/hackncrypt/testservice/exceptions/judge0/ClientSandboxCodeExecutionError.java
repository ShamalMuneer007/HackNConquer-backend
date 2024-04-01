package org.hackncrypt.problemservice.exceptions.judge0;

public class ClientSandboxCodeExecutionError extends RuntimeException {
    public ClientSandboxCodeExecutionError(Exception e,String message) {
        super(message,e);
    }
}
