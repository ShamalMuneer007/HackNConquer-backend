package org.hackncrypt.discussionservice.exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message){
        super(message);
    }
}
