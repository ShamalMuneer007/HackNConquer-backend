package org.hackncrypt.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUserInfoResponse {
    private Page<UserDto> users;
    private int status;
}
