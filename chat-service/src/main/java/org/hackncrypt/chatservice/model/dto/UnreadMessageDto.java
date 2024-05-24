package org.hackncrypt.chatservice.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnreadMessageDto {
    private Long count;
    private Long userId;
    public UnreadMessageDto(Long userId, Long count) {
        this.userId = userId;
        this.count = count;
    }
}
