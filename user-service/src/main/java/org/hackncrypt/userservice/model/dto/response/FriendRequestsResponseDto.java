package org.hackncrypt.userservice.model.dto.response;

import lombok.*;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.entities.FriendRequest;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FriendRequestsResponseDto {
    private UserDto user;
    private String timeStamp;
    public FriendRequestsResponseDto(FriendRequest friendRequest){
        this.user = new UserDto(friendRequest.getSender());
        this.timeStamp = friendRequest.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }
}
