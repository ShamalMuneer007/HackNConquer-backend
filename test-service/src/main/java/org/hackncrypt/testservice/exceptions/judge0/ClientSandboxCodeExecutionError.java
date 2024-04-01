package org.hackncrypt.testservice.exceptions.judge0;

public class ClientSandboxCodeExecutionError extends RuntimeException {
    public ClientSandboxCodeExecutionError(Exception e,String message) {
        super(message,e);
    }
}
