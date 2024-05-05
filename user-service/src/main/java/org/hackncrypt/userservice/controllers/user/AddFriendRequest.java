package org.hackncrypt.userservice.controllers.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFriendRequest {
    private Long FriendUserId;
}
