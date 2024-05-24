package org.hackncrypt.chatservice.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMessagesRequest {
    private Long senderId;
    private Long receiverId;
}
