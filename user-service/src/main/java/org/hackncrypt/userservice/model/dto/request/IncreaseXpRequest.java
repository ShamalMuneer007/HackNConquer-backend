package org.hackncrypt.userservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IncreaseXpRequest {
    private Long userId;
    private int xp;
}
