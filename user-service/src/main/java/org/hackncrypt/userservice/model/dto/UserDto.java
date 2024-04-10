package org.hackncrypt.userservice.model.dto;

import lombok.*;
import org.hackncrypt.userservice.model.entities.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String username;
    private String playerRank;
    private String profileImage;
    private String email;
    private int level;
    private int xp;
    private boolean isPremium;
    private String role;
    private int currentMaxXp;
    public UserDto(User user){
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.isPremium = user.isPremium();
        this.xp = user.getXp();
        this.level = user.getLevel();
        this.profileImage = user.getProfileImageUrl();
        this.username = user.getUsername();
        this.playerRank = user.getPlayerRank();
        this.role = user.getRole().name();
        this.currentMaxXp = user.getCurrentMaxXp();
    }
}
