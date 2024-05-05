package org.hackncrypt.discussionservice.exceptions;

public class DiscussionNotFoundException extends RuntimeException {
    public DiscussionNotFoundException(String message) {
        super(message);
    }
}
