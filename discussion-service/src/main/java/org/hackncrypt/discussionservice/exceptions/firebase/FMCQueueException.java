package org.hackncrypt.discussionservice.exceptions.firebase;

import org.springframework.amqp.AmqpException;

public class FMCQueueException extends RuntimeException {
    public FMCQueueException(AmqpException exception) {
        super(exception);
    }
}
