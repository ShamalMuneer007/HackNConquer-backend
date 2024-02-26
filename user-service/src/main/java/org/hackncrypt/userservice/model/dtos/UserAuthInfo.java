package org.hackncrypt.userservice.model.dtos;

import lombok.*;
import org.hackncrypt.userservice.enums.Role;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserAuthInfo {
    String username;
    Role role;
}
