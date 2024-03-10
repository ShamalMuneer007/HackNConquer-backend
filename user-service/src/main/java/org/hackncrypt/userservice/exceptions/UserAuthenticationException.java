package org.hackncrypt.userservice.exceptions;

public class UserAuthenticationException extends RuntimeException{
    public UserAuthenticationException(String message){
        super(message);
    }
    public UserAuthenticationException(String message,Exception e){
        super(message,e);
    }
}
