package org.hackncrypt.problemservice.exceptions;

public class ClientSandboxCodeExecutionError extends RuntimeException {
    public ClientSandboxCodeExecutionError(Exception e,String message) {
        super(message,e);
    }
}
