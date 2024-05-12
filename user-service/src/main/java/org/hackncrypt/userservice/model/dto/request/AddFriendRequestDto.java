package org.hackncrypt.userservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddFriendRequestDto {
    private Long senderId;
    private Long receiverId;
}
