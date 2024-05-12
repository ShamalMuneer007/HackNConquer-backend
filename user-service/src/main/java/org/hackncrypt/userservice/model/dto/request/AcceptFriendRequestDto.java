package org.hackncrypt.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptFriendRequestDto {
    private Long senderId;
}
